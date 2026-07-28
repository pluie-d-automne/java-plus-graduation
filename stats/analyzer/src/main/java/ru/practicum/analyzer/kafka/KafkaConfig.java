package ru.practicum.analyzer.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("kafka")
@Configuration
@Slf4j
public class KafkaConfig implements KafkaClient {
    private Consumer<Long, SpecificRecordBase> consumer;
    private Properties properties;

    @Override
    public Consumer<Long, SpecificRecordBase> getConsumer(String type) {
        if (consumer == null) {
            initConsumer(type);
        }
        return consumer;
    }


    private void initConsumer(String type) {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperty(type + ".bootstrap.servers"));
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, properties.getProperty(type + ".key_deserializer_class"));
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, properties.getProperty(type + ".value_deserializer_class"));
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, properties.getProperty(type + ".group_id"));
        consumer = new KafkaConsumer<>(config);
    }


    @Override
    public void stop() {
        if (consumer != null) {
            consumer.close();
        }
    }
}
