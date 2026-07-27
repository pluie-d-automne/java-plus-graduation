package ru.practicum.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.collector.service.UserActionHandler;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;
import ru.yandex.practicum.grpc.stats.collector.UserActionControllerGrpc;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final UserActionHandler userActionHandler;

    @Override
    public void collectUserAction(UserActionProto userActionProto, StreamObserver<Empty> responseObserver) {
        log.info("Обрабатываю UserActionProto");
        userActionHandler.handle(userActionProto);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
