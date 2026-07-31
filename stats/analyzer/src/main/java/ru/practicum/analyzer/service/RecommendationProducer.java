package ru.practicum.analyzer.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.analyzer.dto.SimilarEvent;
import ru.practicum.analyzer.repository.InteractionRepository;
import ru.practicum.analyzer.repository.SimilarityRepository;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc.RecommendationsControllerImplBase;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import  ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@GrpcService
@Slf4j
@RequiredArgsConstructor
public class RecommendationProducer extends RecommendationsControllerImplBase {
    private final SimilarityRepository similarityRepository;
    private final InteractionRepository interactionRepository;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto userPredictionsRequestProto,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        Long userId = userPredictionsRequestProto.getUserId();
        Integer max_results = userPredictionsRequestProto.getMaxResults();

        List<Long> recentUserEventIds = interactionRepository.findRecentEventsByUserId(userId, max_results);
        Set<Long> userEventIds = interactionRepository.findEventIdByUserId(userId);
        List<SimilarEvent> nMostSimilarEvents = similarityRepository.findAllSimilarEventsByList(recentUserEventIds)
                .stream()
                .filter(event -> !userEventIds.contains(event.eventId()))
                .limit(max_results)
                .toList();

        for (SimilarEvent event: nMostSimilarEvents) {
            // N просмотренных мероприятий, максимально похожих на предсказываемое, с которыми пользователь уже взаимодействовал.
            Map<Long, Double> nearestNSimilarEvents = similarityRepository.findAllSimilarEvents(event.eventId())
                    .stream()
                    .filter(eventToCheck -> userEventIds.contains(eventToCheck.eventId()))
                    .limit(max_results)
                    .collect(Collectors.toMap(eventToCheck -> eventToCheck.eventId(), eventToCheck -> eventToCheck.similarity()));
            // Получаем оценки пользователя, поставленные этим мероприятиям
            List<SimilarEvent> nearestNSimilarEventMarks =  interactionRepository.findEventsByUserIdAndEvents(userId, nearestNSimilarEvents.keySet().stream().toList());
            // Вычислить сумму взвешенных оценок.
            double weightedMarks = nearestNSimilarEventMarks
                    .stream()
                    .mapToDouble(eventToCalc -> eventToCalc.similarity() * nearestNSimilarEvents.get(eventToCalc.eventId()))
                    .sum();
            // Вычислить сумму коэффициентов подобия
            double weightSum = nearestNSimilarEvents.values().stream().mapToDouble(x -> x).sum();
            // Делим одно на другое
            double result = weightedMarks/weightSum;

            responseObserver.onNext(RecommendedEventProto.newBuilder()
                    .setEventId(event.eventId())
                    .setScore(result)
                    .build());
        }
        responseObserver.onCompleted();
    }


    @Override
    public void getSimilarEvents (SimilarEventsRequestProto similarEventsRequestProto,
                                  StreamObserver<RecommendedEventProto> responseObserver) {
        Long userId = similarEventsRequestProto.getUserId();
        Long eventId  = similarEventsRequestProto.getEventId();
        int max_results = similarEventsRequestProto.getMaxResults();
        List<SimilarEvent> allSimilarEvents = similarityRepository.findAllSimilarEvents(eventId);
        Set<Long> userEventIds = interactionRepository.findEventIdByUserId(userId);
        int i = 0;

        for (SimilarEvent event: allSimilarEvents) {
            if (!userEventIds.contains(event.eventId())) {
                responseObserver.onNext(RecommendedEventProto.newBuilder()
                        .setEventId(event.eventId())
                        .setScore(event.similarity())
                        .build());
                i += 1;
            }

            if (i==max_results) {
                break;
            }
        }

        responseObserver.onCompleted();
    }


    @Override
    public void getInteractionsCount (InteractionsCountRequestProto interactionsCountRequestProto,
                                      StreamObserver<RecommendedEventProto> responseObserver) {
        List<Long> eventIds = interactionsCountRequestProto.getEventIdList();
        List<SimilarEvent> allEvents = interactionRepository.getInteractionsCount(eventIds);

        for (SimilarEvent event: allEvents) {
            responseObserver.onNext(RecommendedEventProto.newBuilder()
                    .setEventId(event.eventId())
                    .setScore(event.similarity())
                    .build());
        }

        responseObserver.onCompleted();
    }
}
