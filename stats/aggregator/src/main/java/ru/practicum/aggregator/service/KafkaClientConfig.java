package ru.practicum.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
@ConfigurationProperties("kafka")
public class KafkaClientConfig implements KafkaClient {
    private Properties properties;
    private Consumer<Long, SpecificRecordBase> consumer;
    private Producer<Long, SpecificRecordBase> producer;


    @Override
    public Consumer<Long, SpecificRecordBase> getConsumer() {
        if (consumer == null) {
            initConsumer();
        }

        return consumer;
    }


    @Override
    public Producer<Long, SpecificRecordBase> getProducer() {
        if (producer == null) {
            initProducer();
        }

        return producer;
    }

    private void initConsumer() {
        Properties config = new Properties();
        String bootstrap_servers = properties.getProperty("consumer.bootstrap_servers");
        String key_serializer_class = properties.getProperty("consumer.key_deserializer_class");
        String value_serializer_class = properties.getProperty("consumer.value_deserializer_class");
        String group_id = properties.getProperty("consumer.group_id");
        log.info("Init Kafka consumer with bootstrap_servers: {}, key_deserializer: {}, value_deserializer: {}, group_id: {}",
                bootstrap_servers, key_serializer_class, value_serializer_class, group_id);
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, key_serializer_class);
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, value_serializer_class);
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group_id);
        consumer = new KafkaConsumer<>(config);
    }


    private void initProducer() {
        Properties config = new Properties();
        String bootstrap_servers = properties.getProperty("producer.bootstrap_servers");
        String key_serializer_class = properties.getProperty("producer.key_serializer_class");
        String value_serializer_class = properties.getProperty("producer.value_serializer_class");
        log.info("Init Kafka producer with bootstrap_servers: {}, key_serializer: {}, value_serializer: {}",
                bootstrap_servers, key_serializer_class, value_serializer_class);
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,  bootstrap_servers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, key_serializer_class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, value_serializer_class);

        producer = new KafkaProducer<>(config);
    }


    @Override
    public Properties getProperties() {
        return properties;
    };


    @Override
    public void stop() {
        if (producer != null) {
            producer.close();
        }
        if (consumer !=null) {
            consumer.close();
        }
    }
}
