package ru.practicum.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@Slf4j
@ConfigurationProperties("kafka")
public class KafkaClientConfig {
    private Properties properties;

    @Bean
    KafkaClient getClient() {
        return new KafkaClient() {

            private Producer<Long, SpecificRecordBase> producer;

            @Override
            public Producer<Long, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {
                Properties config = new Properties();
                String bootstrap_servers = properties.getProperty("bootstrap.servers");
                String key_serializer_class = properties.getProperty("key_serializer_class");
                String value_serializer_class = properties.getProperty("value_serializer_class");

                log.info("Init Kafka producer with bootstrap_servers: {}, key_serializer: {}, value_serializer: {}",
                        bootstrap_servers, key_serializer_class, value_serializer_class);
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, key_serializer_class);
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, value_serializer_class);

                producer = new KafkaProducer<>(config);
            }

            @Override
            public void stop() {
                if (producer != null) {
                    producer.close();
                }
            }

            @Override
            public Properties getProperties() {
                return properties;
            }
        };
    }
}
