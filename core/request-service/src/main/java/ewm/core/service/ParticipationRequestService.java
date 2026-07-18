package ewm.core.service;

import ewm.core.dto.ConfirmedRequestCount;
import ewm.core.dto.EventRequestStatusUpdateRequest;
import ewm.core.dto.EventRequestStatusUpdateResult;
import ewm.core.dto.ParticipationRequestDto;
import ewm.core.dto.ParticipationStatus;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> getRequestByUserId(Long userId);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest requestUpdate);

    List<ParticipationRequestDto> getRequestsByIds(List<Long> requestIds);

    List<ConfirmedRequestCount> findAllConfirmedRequestsByEventId(List<Long> eventIds);

    Long countByEventIdAndStatus(Long eventId, ParticipationStatus status);
}
