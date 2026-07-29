package ru.practicum.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SimilarityAggregation {
    private Map<Long, Map<Long, Double>> eventUserWeights;  // Map<Event, Map<User, Weight>>
    private Map<Long, Double> eventWeight; // Map<Event, S_event>
    private Map<Long,  Map<Long, Double>> eventPairMinWeightSum; // Map<Event, Map<Event, S_min>>

    public SimilarityAggregation() {
        eventUserWeights = new HashMap<>();
        eventWeight = new HashMap<>();
        eventPairMinWeightSum = new HashMap<>();
    }


    public List<EventSimilarityAvro> getUpdatedSimilarities(UserActionAvro userAction) {
        List<EventSimilarityAvro> result = new ArrayList<>();
        Long newUserId = userAction.getUserId();
        Long newEventId = userAction.getEventId();
        Double newEventUserWeight = getActionWeight(userAction.getActionType());

        if (eventUserWeights.containsKey(newEventId)) {
            // Если очередное взаимодействие с мероприятием, обновляем сходство
            Double oldEventUserWeight = eventUserWeights.get(newEventId).get(newUserId);

            // 1. Проверяем, что событие увеличило вес мероприятия для пользователя
            if (oldEventUserWeight < newEventUserWeight) {

                // 2. Обновляем вес действия в матрице весов
                eventUserWeights
                        .computeIfAbsent(newEventId, e -> new HashMap<>())
                        .put(newUserId, newEventUserWeight);

                // 3. Обновляем сумму весов для этого события
                eventWeight.put(newEventId, eventWeight.get(newEventId) + newEventUserWeight - oldEventUserWeight);

                // 4. Пересчитываем для него сходства с другими мероприятиями
                for (Long eventId : getEventsByUser(newUserId)) {
                    if (! eventId.equals(newEventId)) {
                        Double sum = Math.min(eventUserWeights.get(eventId).get(newUserId), newEventUserWeight);
                        Double sumOld = Math.min(eventUserWeights.get(eventId).get(newUserId), oldEventUserWeight);

                        putEventPairMinWeightSum(eventId,
                                newEventId,
                                getEventPairMinWeightSum(eventId, newEventId) + sum - sumOld);

                        result.add(EventSimilarityAvro.newBuilder()
                                .setEventA(Math.min(eventId, newEventId))
                                .setEventB(Math.max(eventId, newEventId))
                                .setScore(getEventPairMinWeightSum(eventId, newEventId) / (Math.sqrt(eventWeight.get(newEventId)) * Math.sqrt(eventWeight.get(eventId))))
                                .setTimestamp(userAction.getTimestamp())
                                .build());
                    }
                }
            }

            return result;
        } else {// Если мероприятие новое, рассчитываем его сходство с остальными
            // 1. Добавляем событие в матрицу весов
            eventUserWeights.put(newEventId, Map.of(newUserId, newEventUserWeight));

            // 2. Сохраняем для него сумму весов
            eventWeight.put(newEventId, newEventUserWeight);

            // 3. Рассчитываем для него сходства с другими мероприятиями
            for (Long eventId : getEventsByUser(newUserId)) {
                if (! eventId.equals(newEventId)) {
                    Double sum = Math.min(eventUserWeights.get(eventId).get(newUserId), newEventUserWeight);

                    putEventPairMinWeightSum(eventId,
                            newEventId,
                            sum);

                    result.add(EventSimilarityAvro.newBuilder()
                            .setEventA(Math.min(eventId, newEventId))
                            .setEventB(Math.max(eventId, newEventId))
                            .setScore(sum / (Math.sqrt(eventWeight.get(newEventId)) * Math.sqrt(eventWeight.get(eventId))))
                            .setTimestamp(userAction.getTimestamp())
                            .build());
                }
            }

            return result;
        }
    }


    private List<Long> getEventsByUser(Long userId) {
        return eventUserWeights.keySet().stream().filter(eventId -> eventUserWeights.get(eventId).containsKey(userId)).toList();
    }


    private Double getActionWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ActionTypeAvro.ACTION_LIKE -> 1D;
            case ActionTypeAvro.ACTION_REGISTER -> 0.8;
            case ActionTypeAvro.ACTION_VIEW -> 0.4;
            default -> 0D;
        };
    }


    private void putEventPairMinWeightSum(Long eventA, Long eventB, Double sum) {
        Long first  = Math.min(eventA, eventB);
        Long second = Math.max(eventA, eventB);

        eventPairMinWeightSum
                .computeIfAbsent(first, e -> new HashMap<>())
                .put(second, sum);
    }


    private double getEventPairMinWeightSum(Long eventA, Long eventB) {
        Long first  = Math.min(eventA, eventB);
        Long second = Math.max(eventA, eventB);

        return eventPairMinWeightSum
                .computeIfAbsent(first, e -> new HashMap<>())
                .getOrDefault(second, 0.0);
    }
}
