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
        log.info("Обрабатываю действие пользователя {} с событием {} с весом {}", newUserId, newEventId, newEventUserWeight);

        if (eventUserWeights.containsKey(newEventId)) {
            log.info("Очередное взаимодействие с мероприятием {} => обновляем сходство", newEventId);
            Double oldEventUserWeight = eventUserWeights.get(newEventId).get(newUserId);
            oldEventUserWeight = oldEventUserWeight == null ? 0D : oldEventUserWeight;

            if (oldEventUserWeight < newEventUserWeight) {
                log.info("1. Cобытие увеличило вес мероприятия {} для пользователя {}: {} -> {}",
                        newEventId, newUserId, oldEventUserWeight, newEventUserWeight);

                eventUserWeights
                        .computeIfAbsent(newEventId, e -> new HashMap<>())
                        .put(newUserId, newEventUserWeight);
                log.info("2. Вес действия в матрице весов обновлён: {}", eventUserWeights.get(newEventId).get(newUserId));


                eventWeight.put(newEventId, eventWeight.get(newEventId) + newEventUserWeight - oldEventUserWeight);
                log.info("3. Обновлена сумма весов для этого события: {} -> {}", oldEventUserWeight, eventWeight.get(newEventId));

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
                        log.info("4. Пересчитано сходство: {}", result.getLast());
                    }
                }
            }

            return result;
        } else {
            log.info("Мероприятие {} новое => рассчитываем его сходство с остальными", newEventId);

            eventUserWeights
                    .computeIfAbsent(newEventId, e -> new HashMap<>())
                    .put(newUserId, newEventUserWeight);
            log.info("1. Cобытие добавлено в матрицу весов: {}", eventUserWeights.get(newEventId));

            eventWeight.put(newEventId, newEventUserWeight);
            log.info("2. Для события сохранена сумма весов: {}", eventWeight.get(newEventId));

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
                    log.info("3. Пересчитано сходство: {}", result.getLast());
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
