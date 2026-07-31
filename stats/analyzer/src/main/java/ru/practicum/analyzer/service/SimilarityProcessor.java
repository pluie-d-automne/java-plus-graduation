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
import ru.practicum.analyzer.model.Similarity;
import ru.practicum.analyzer.repository.SimilarityRepository;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
@ConfigurationProperties("kafka")
public class SimilarityProcessor implements Runnable {
    private final KafkaClient client;
    private final Properties properties;
    private final SimilarityRepository similarityRepository;

    @Override
    public void run() {
        Consumer consumer = client.getConsumer("similarities");

        // Хук для завершения JVM
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            // подписка на топики
            String topic = properties.getProperty("similarities.topic");
            consumer.subscribe(List.of(topic));
            // цикл опроса
            while (true) {
                ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    // Читает и записывает в БД

                    EventSimilarityAvro eventSimilarityAvro = (EventSimilarityAvro) record.value();

                    Optional<Similarity> similarityFound = similarityRepository.findByEvent1AndEvent2(eventSimilarityAvro.getEventA(), eventSimilarityAvro.getEventB());
                    if (similarityFound.isEmpty()) {
                        Similarity similarity = Similarity.builder()
                                .event1(eventSimilarityAvro.getEventA())
                                .event2(eventSimilarityAvro.getEventB())
                                .similarity(eventSimilarityAvro.getScore())
                                .timestamp(LocalDateTime.ofInstant(eventSimilarityAvro.getTimestamp(), ZoneId.of("UTC")))
                                .build();

                        log.info("Сохраняю новое значение сходства для событий: {}", similarity);
                        Similarity newSimilarity = similarityRepository.save(similarity);
                        log.info("Новое значение сходства для событий сохранено: {}", newSimilarity);
                    } else {
                        Similarity similarity = similarityFound.get();
                        log.info("Для событий уже есть сохранённые веса: {}", similarity);
                        if (eventSimilarityAvro.getScore() != similarity.getSimilarity().doubleValue()) {
                            similarity.setSimilarity(eventSimilarityAvro.getScore());
                            similarity.setTimestamp(LocalDateTime.ofInstant(eventSimilarityAvro.getTimestamp(), ZoneId.of("UTC")));
                            Similarity newSimilarity = similarityRepository.save(similarity);
                            log.info("Значение сходства изменилось => Запись в БД обновлена: {}", newSimilarity);
                        } else {
                            log.info("Значение сходства не изменилось => ничего не меняем в БД");
                        }
                    }
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки похожестей", e);
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
