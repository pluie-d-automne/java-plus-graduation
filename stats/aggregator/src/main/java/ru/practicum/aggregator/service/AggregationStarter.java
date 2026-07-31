package ru.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final Consumer consumer;
    private final Producer producer;
    private final SimilarityAggregation agg;
    private Properties properties;


    @Autowired
    public AggregationStarter(KafkaClient client) {
        this.consumer = client.getConsumer();
        this.producer = client.getProducer();
        this.agg = new SimilarityAggregation();
        this.properties = client.getProperties();
    }


    public void start() {
        // Хук для завершения JVM
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(properties.getProperty("consumer.topic")));
            // Цикл обработки событий
            while (true) {
                ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    if (record.value() instanceof UserActionAvro) {
                        UserActionAvro userAction = (UserActionAvro) record.value();
                        List<EventSimilarityAvro> eventSimilarities = agg.getUpdatedSimilarities(userAction);
                        for (EventSimilarityAvro similarity : eventSimilarities) {
                            ProducerRecord<Long, SpecificRecordBase> recordToSend = new ProducerRecord<>(properties.getProperty("producer.topic"),
                                    similarity.getEventA(),
                                    similarity);
                            log.info("Отправляю новое значение сходства {} для событий {} и {} в Kafka.",
                                    similarity.getScore(), similarity.getEventA(), similarity.getEventB());
                            producer.send(recordToSend);
                        }
                    }
                }
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки действий пользователя", e);
        } finally {

            try {
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедиться,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы

                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                producer.flush();
                // здесь нужно вызвать метод консьюмера для фиксации смещений
                consumer.commitSync();

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}
