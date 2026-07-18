package ewm.core.service;

import ewm.core.client.EventClient;
import ewm.core.dto.ConfirmedRequestCount;
import ewm.core.dto.EventFullDto;
import ewm.core.dto.EventRequestStatusUpdateRequest;
import ewm.core.dto.EventRequestStatusUpdateResult;
import ewm.core.dto.EventState;
import ewm.core.dto.ParticipationStatus;
import ewm.core.dto.UserShortDto;
import ewm.core.exception.ConflictException;
import ewm.core.exception.NotFoundException;
import ewm.core.exception.ValidationException;
import ewm.core.dto.ParticipationRequestDto;
import ewm.core.client.UserClient;
import ewm.core.mapper.ParticipationRequestMapper;
import ewm.core.model.ParticipationRequest;
import ewm.core.repository.ParticipationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final UserClient userClient;
    private final EventClient eventClient;
    private final ParticipationRequestRepository requestRepository;
    private final ParticipationRequestMapper requestMapper;


    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        findUserById(userId);

        List<ParticipationRequest> requests = requestRepository.findByRequesterId(userId);

        log.info("Получен список заявок на участия в событиях пользователя с id = {}", userId);
        return requests.stream()
                .map(requestMapper::mapToRequestDto)
                .toList();
    }


    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        findUserById(userId);

        EventFullDto eventFullDto = findEventById(eventId);

        ParticipationRequest request = new ParticipationRequest();
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);

        if (requestRepository.existsByRequesterAndEventId(userId, eventId)) {
            throw new ConflictException("Participation request already exists");
        }

        if (eventFullDto.getInitiator().id().equals(userId)) {
            throw new ConflictException("The initiator of the event cannot add a request to participate in their own event");
        }

        if (!eventFullDto.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("The event has not been published yet");
        }

        if (eventFullDto.getParticipantLimit() != 0 && eventFullDto.getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("The participant limit for this event has been reached");
        }

        if (eventFullDto.getRequestModeration() == false || eventFullDto.getParticipantLimit() == 0) {
            request.setStatus(ParticipationStatus.CONFIRMED);
        } else {
            request.setStatus(ParticipationStatus.PENDING);
        }

        request.setRequesterId(userId);
        request.setEventId(eventId);

        ParticipationRequest saveRequest = requestRepository.save(request);

        log.info("Запрос на участие в событии добавлен");

        return requestMapper.mapToRequestDto(saveRequest);
    }


    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        findUserById(userId);

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequesterId().equals(userId)) {
            throw new ValidationException("You can only cancel your own request");
        }

        request.setStatus(ParticipationStatus.CANCELED);
        requestRepository.save(request);

        log.info("Заявка на событие отменена");

        return requestMapper.mapToRequestDto(request);
    }


    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        // Проверяем, что пользователь существует
        findUserById(userId);

        // Проверяем, что событие существует и принадлежит пользователю
        EventFullDto eventFullDto = findEventById(eventId);

        if (!eventFullDto.getInitiator().id().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " not found for user with id=" + userId);
        }

        List<ParticipationRequest> requests = requestRepository.findByEventId(eventId);

        log.info("Получен список заявок на участие в событии с id = {}", eventId);
        return requests.stream()
                .map(requestMapper::mapToRequestDto)
                .toList();
    }


    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest requestUpdate) {
        // Проверяем пользователя и событие
        findUserById(userId);
        EventFullDto eventFullDto = findEventById(eventId);

        if (!eventFullDto.getInitiator().id().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " not found for user with id=" + userId);
        }

        // Получаем список заявок
        List<ParticipationRequest> requests = requestRepository.findAllById(requestUpdate.requestIds());

        // Создаём списки для результатов
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        // Текущее количество подтверждённых заявок
        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);

        if (confirmedRequests >= eventFullDto.getParticipantLimit()) {
            throw new ConflictException("The participant limit for this event has been reached");
        }

        for (ParticipationRequest request : requests) {
            if (!request.getEventId().equals(eventId)) {
                throw new ValidationException("Request does not belong to this event");
            }
            if (request.getStatus() != ParticipationStatus.PENDING) {
                throw new ConflictException("Request status must be PENDING");
            }

            if ("REJECTED".equals(requestUpdate.status())) {
                request.setStatus(ParticipationStatus.REJECTED);
                rejected.add(requestMapper.mapToRequestDto(request));
            } else if ("CONFIRMED".equals(requestUpdate.status())) {
                if (eventFullDto.getParticipantLimit() == 0 || confirmedRequests < eventFullDto.getParticipantLimit()) {
                    request.setStatus(ParticipationStatus.CONFIRMED);
                    confirmed.add(requestMapper.mapToRequestDto(request));
                    confirmedRequests++;
                } else {
                    request.setStatus(ParticipationStatus.REJECTED);
                    rejected.add(requestMapper.mapToRequestDto(request));
                }
            }
        }

        requestRepository.saveAll(requests);
        log.info("Обновлён статус заявок на участие в событии с id = {}", eventId);

        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }


    @Override
    public List<ParticipationRequestDto> getRequestsByIds(List<Long> requestIds) {
        return requestRepository.findAllById(requestIds).stream()
                .map(requestMapper::mapToRequestDto)
                .toList();
    }


    @Override
    public List<ConfirmedRequestCount> findAllConfirmedRequestsByEventId(List<Long> eventIds) {
        return requestRepository.findAllConfirmedRequestsByEventId(eventIds);
    }


    @Override
    public Long countByEventIdAndStatus(Long eventId, ParticipationStatus status) {
        return countByEventIdAndStatus(eventId, status);
    }


    private UserShortDto findUserById(Long userId) {
        List<UserShortDto> userShortDtos = userClient.getUsersByIds(List.of(userId));

        if (userShortDtos.isEmpty()) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        return userShortDtos.getFirst();
    }


    private EventFullDto findEventById(Long eventId) {
        List<EventFullDto> eventFullDtos = eventClient.getEventsByIds(List.of(eventId));

        if (eventFullDtos.isEmpty()) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        return eventFullDtos.getFirst();
    }

}
