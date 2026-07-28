package ru.practicum.analyzer.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc.RecommendationsControllerImplBase;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import  ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;

import java.util.stream.Stream;

@GrpcService
@Slf4j
public class RecommendationProducer extends RecommendationsControllerImplBase {
    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto userPredictionsRequestProto,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        responseObserver.onNext(RecommendedEventProto.newBuilder()
                .setEventId(123L)
                .setScore(0.8)
                .build());
        responseObserver.onCompleted();
    }


    @Override
    public void getSimilarEvents (SimilarEventsRequestProto similarEventsRequestProto,
                                  StreamObserver<RecommendedEventProto> responseObserver) {
        responseObserver.onNext(RecommendedEventProto.newBuilder()
                .setEventId(123L)
                .setScore(0.8)
                .build());
        responseObserver.onCompleted();

    }


    @Override
    public void getInteractionsCount (InteractionsCountRequestProto interactionsCountRequestProto,
                                      StreamObserver<RecommendedEventProto> responseObserver) {
        responseObserver.onNext(RecommendedEventProto.newBuilder()
                .setEventId(123L)
                .setScore(0.8)
                .build());
        responseObserver.onCompleted();
    }
}
