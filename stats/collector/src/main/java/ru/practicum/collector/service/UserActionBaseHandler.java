package ru.practicum.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.collector.utils.EnumMapper;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;
import java.util.Properties;

@Slf4j
public class UserActionBaseHandler <D extends SpecificRecordBase> implements UserActionHandler {
    private final KafkaProducer producer;
    private Properties properties;

    public UserActionBaseHandler(KafkaClient client, KafkaProducer producer) {
        this.producer = producer;
        this.properties = client.getProperties();
    }

    @Override
    public void handle(UserActionProto userActionProto) {
        SpecificRecordBase userActionAvro = mapToAvro(userActionProto);
        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(properties.getProperty("topic"),
                userActionProto.getEventId(),
                userActionAvro);

        log.info("Отправляю действие пользователя в топик {}", properties.getProperty("topic"));
        producer.send(record);
    }

    private SpecificRecordBase mapToAvro(UserActionProto userActionProto) {
        return UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(), userActionProto.getTimestamp().getNanos()))
                .setActionType(EnumMapper.map(userActionProto.getActionType(), ActionTypeAvro.class))
                .build();
    }
}
