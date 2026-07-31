package ru.practicum.analyzer.kafka;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public class EventSimilarityDeserializer extends BaseAvroDeserializer<EventSimilarityAvro>  {
    public EventSimilarityDeserializer() {
        super(EventSimilarityAvro.getClassSchema());
    }
}
