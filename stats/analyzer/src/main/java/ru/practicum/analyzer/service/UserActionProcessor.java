package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.kafka.KafkaClient;
import ru.practicum.analyzer.model.Interaction;
import ru.practicum.analyzer.repository.InteractionRepository;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
@ConfigurationProperties("kafka")
public class UserActionProcessor implements Runnable {
    private final KafkaClient client;
    private final Properties properties;
    private final InteractionRepository interactionRepository;

    @Override
    public void run() {
        Consumer consumer = client.getConsumer("actions");

        // Хук для завершения JVM
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            // подписка на топики
            String topic = properties.getProperty("actions.topic");
            consumer.subscribe(List.of(topic));
            // цикл опроса
            while (true) {
                ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    // Читает и записывает в БД
                    UserActionAvro userActionAvro = (UserActionAvro) record.value();
                    Interaction interaction = Interaction.builder()
                            .eventId(userActionAvro.getEventId())
                            .userId(userActionAvro.getUserId())
                            .rating(getActionWeight(userActionAvro.getActionType()))
                            .timestamp(LocalDateTime.ofInstant(userActionAvro.getTimestamp(), ZoneId.of("UTC")))
                            .build();

                    log.info("Сохраняю новое взаимодействие пользователя с событием: {}", interaction);
                    Interaction newInteraction = interactionRepository.save(interaction);
                    log.info("Новое взаимодействие пользователя с событием сохранено: {}", newInteraction);

                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки действий пользователей", e);
        } finally {

            try {
                consumer.commitSync();

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }


    private Double getActionWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ActionTypeAvro.ACTION_LIKE -> 1D;
            case ActionTypeAvro.ACTION_REGISTER -> 0.8;
            case ActionTypeAvro.ACTION_VIEW -> 0.4;
            default -> 0D;
        };
    }
}
