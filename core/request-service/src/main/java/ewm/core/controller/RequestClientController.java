package ewm.core.controller;

import ewm.core.client.RequestClient;
import ewm.core.dto.ConfirmedRequestCount;
import ewm.core.dto.EventRequestStatusUpdateRequest;
import ewm.core.dto.EventRequestStatusUpdateResult;
import ewm.core.dto.ParticipationRequestDto;
import ewm.core.dto.ParticipationStatus;
import ewm.core.service.ParticipationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@FeignClient(name = "request-client")
@RequestMapping("/request-client")
public class RequestClientController implements RequestClient {
    private final ParticipationRequestService requestService;

    @Override
    @GetMapping
    public List<ParticipationRequestDto> getRequestsByIds(@RequestParam(name="ids") List<Long> requestIds) {
        log.info("Need to get requestDto list by ids: {}", requestIds);
        List<ParticipationRequestDto> requestDtos = requestService.getRequestsByIds(requestIds);
        log.info("Return list of requestDto: {}", requestDtos);
        return requestDtos;
    }

    @Override
    @GetMapping("/user/{userId}/event/{eventId}")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Need to get event requests for userId: {}; eventId: {}.", userId, eventId);
        List<ParticipationRequestDto> requestDtos = requestService.getEventRequests(userId, eventId);
        log.info("Return list of requestDto: {}", requestDtos);
        return requestDtos;
    }

    @Override
    @PatchMapping("/user/{userId}/event/{eventId}")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest requestUpdate) {
        log.info("Need to update request status for userId: {}, eventId: {}, updates: {}", userId, eventId, requestUpdate);
        EventRequestStatusUpdateResult result = requestService.updateRequestStatus(userId, eventId, requestUpdate);
        log.info("Return result: {}", result);
        return result;
    }

    @Override
    @GetMapping("/event")
    public List<ConfirmedRequestCount> findAllConfirmedRequestsByEventId(@RequestParam(name="ids") List<Long> eventIds) {
        log.info("Need to find all confirmed requests by eventIds: {}", eventIds);
        List<ConfirmedRequestCount> result = requestService.findAllConfirmedRequestsByEventId(eventIds);
        log.info("Return result: {}", result);
        return result;
    }

    @Override
    @GetMapping("/event/{eventId}")
    public Long countByEventIdAndStatus(@PathVariable Long eventId,
                                        @RequestParam(name="status") ParticipationStatus status) {
        log.info("Need to count requests by eventId {} and status {}.", eventId, status);
        Long cnt = requestService.countByEventIdAndStatus(eventId, status);
        log.info("There are {} requests for eventId {} with status {}.", cnt, eventId, status);
        return cnt;
    }
}
