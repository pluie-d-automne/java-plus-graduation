package ru.practicum.collector.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

public interface KafkaClient {
    Producer<Long, SpecificRecordBase> getProducer();

    Properties getProperties();

    void stop();
}
