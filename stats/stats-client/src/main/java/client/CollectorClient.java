package client;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;


import com.google.protobuf.Timestamp;


@Slf4j
@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class CollectorClient {
    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub client;


    public void collectUserAction(Long eventId, Long userId, String actionType, Timestamp ts) {
        UserActionProto request = UserActionProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setActionType(mapActionType(actionType))
                .setTimestamp(ts)
                .build();
         client.collectUserAction(request);

    }


    private ActionTypeProto mapActionType(String actionName) {
        return switch (actionName) {
            case "ACTION_VIEW" -> ActionTypeProto.ACTION_VIEW;
            case "ACTION_REGISTER" -> ActionTypeProto.ACTION_REGISTER;
            case "ACTION_LIKE" -> ActionTypeProto.ACTION_LIKE;
            default -> throw new RuntimeException("Unexpected action type name");
        };
    }

}
