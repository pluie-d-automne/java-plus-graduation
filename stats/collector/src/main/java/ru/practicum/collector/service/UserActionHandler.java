package ru.practicum.collector.service;

import ru.yandex.practicum.grpc.stats.action.UserActionProto;

public interface UserActionHandler {
    void handle(UserActionProto userAction);
}
