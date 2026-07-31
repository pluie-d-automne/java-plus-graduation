package ru.practicum.aggregator.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

public interface KafkaClient {
    Producer<Long, SpecificRecordBase> getProducer();

    Consumer<Long, SpecificRecordBase> getConsumer();

    void stop();

    Properties getProperties();
}
