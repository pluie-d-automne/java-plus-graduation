package ewm.core.client;

import ewm.core.dto.ConfirmedRequestCount;
import ewm.core.dto.EventRequestStatusUpdateRequest;
import ewm.core.dto.EventRequestStatusUpdateResult;
import ewm.core.dto.ParticipationRequestDto;
import ewm.core.dto.ParticipationStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "request-client")
public interface RequestClient {
    @GetMapping
    List<ParticipationRequestDto> getRequestsByIds(@RequestParam(name="ids") List<Long> requestIds);

    @GetMapping("/user/{userId}/event/{eventId}")
    List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId, @PathVariable Long eventId);

    @PatchMapping("/user/{userId}/event/{eventId}")
    EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest requestUpdate
    );

    @GetMapping("/event")
    List<ConfirmedRequestCount> findAllConfirmedRequestsByEventId(@RequestParam(name="ids") List<Long> eventIds);

    @GetMapping("/event/{eventId}")
    Long countByEventIdAndStatus(@PathVariable Long eventId, @RequestParam(name="status") ParticipationStatus status);
}
