package ru.practicum.collector.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private final KafkaClient client;
    private final Producer<Long, SpecificRecordBase> producer;

    public KafkaProducer(KafkaClient client) {
        this.client = client;
        producer = client.getProducer();
    }

    public void stop() {
        client.stop();
    }

    public void send( ProducerRecord<Long, SpecificRecordBase> record) {
        producer.send(record);
    }
}
