package ewm.core.event.service;

import client.AnalyzerClient;
import client.CollectorClient;
import com.querydsl.core.BooleanBuilder;
import ewm.core.category.model.Category;
import ewm.core.category.repository.CategoryRepository;
import ewm.core.client.RequestClient;
import ewm.core.dto.ConfirmedRequestCount;
import ewm.core.dto.EventFullDto;
import ewm.core.dto.ParticipationRequestDto;
import ewm.core.dto.ParticipationStatus;
import ewm.core.dto.UserShortDto;
import ewm.core.exception.ConflictException;
import ewm.core.exception.NotFoundException;
import ewm.core.exception.ValidationException;
import ewm.core.event.dto.*;
import ewm.core.event.mapper.EventMapper;
import ewm.core.event.model.Location;
import ewm.core.event.model.Event;
import ewm.core.dto.EventState;
import ewm.core.event.model.QEvent;
import ewm.core.client.UserClient;
import ewm.core.event.repository.EventRepository;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final RequestClient requestClient;
    private final EventRepository eventRepository;
    private final UserClient userClient;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final AnalyzerClient analyzerClient;
    public final CollectorClient collectorClient;


    @Override
    public List<EventShortDto> getEventsPrivate(Long userId, Integer from, Integer size) {
        log.info("Getting events for user id={}, from={}, size={}", userId, from, size);

        getUserOrThrow(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream().map(Event::getId).toList();

        Map<Long, Long> confirmedRequestsMap = requestClient.findAllConfirmedRequestsByEventId(eventIds).stream()
                .collect(Collectors.toMap(ConfirmedRequestCount::eventId, ConfirmedRequestCount::count));
        Map<Long, Double> ratings = getRatingsMap(events);

        return events.stream()
                .map(eventMapper::toShortDto)
                .peek(shortDto -> {
                    shortDto.setConfirmedRequests(confirmedRequestsMap.getOrDefault(shortDto.getId(), 0L));
                    shortDto.setRating(ratings.getOrDefault(shortDto.getId(), 0D));
                })
                .toList();
    }


    @Override
    @Transactional
    public EventFullDto addEventPrivate(Long userId, NewEventDto dto) {
        log.info("Adding event for user id={}", userId);

       getUserOrThrow(userId);

        if (dto.eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        Category category = categoryRepository.findById(dto.category())
                .orElseThrow(() -> new NotFoundException("Category with id= " + dto.category() + " was not found"));

        // Используем маппер для создания события
        Event event = eventMapper.toEvent(dto);

        event.setCategory(category);
        event.setInitiatorId(userId);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        log.info("Event created successfully: id={}", saved.getId());

        return eventMapper.toFullDto(saved);
    }


    @Override
    public EventFullDto getEventByIdPrivate(Long userId, Long eventId, String url) {
        log.info("Getting event id={} for user id={}", eventId, userId);

        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiatorId().equals(userId)) {
            throw new ConflictException("Event does not belong to user");
        }

        LocalDateTime start = event.getPublishedOn() != null ? event.getPublishedOn() : event.getCreatedOn();

        EventFullDto fullDto = eventMapper.toFullDto(event);

        fullDto.setConfirmedRequests(requestClient.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED));
        fullDto.setRating(getRatingsMap(List.of(event)).getOrDefault(eventId, 0D));

        return fullDto;
    }


    @Override
    @Transactional
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest dto) {
        log.info("Updating event id={} for user id={}", eventId, userId);

        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiatorId().equals(userId)) {
            throw new ConflictException("Event does not belong to user");
        }

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (dto.eventDate() != null &&
                dto.eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        // Используем маппер для обновления
        eventMapper.updateEventMap(dto, event);

        // Обрабатываем stateAction отдельно
        if (dto.stateAction() != null) {
            switch (dto.stateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }

        Event updated = eventRepository.save(event);
        log.info("Event updated successfully: id={}", updated.getId());

        return eventMapper.toFullDto(updated);
    }


    @Override
    public List<EventShortDto> getEventsPublic(PublicEventParamDto eventParamDto) {
        if (eventParamDto.rangeStart() != null && eventParamDto.rangeEnd() != null &&
                eventParamDto.rangeStart().isAfter(eventParamDto.rangeEnd())) {
            throw new ValidationException("End date cannot be before start date");
        }

        QEvent event = QEvent.event;
        BooleanBuilder paramFilter = new BooleanBuilder();

        if (eventParamDto.text() != null && !eventParamDto.text().isBlank()) {
            paramFilter.and(event.annotation.containsIgnoreCase(eventParamDto.text())
                    .or(event.description.containsIgnoreCase(eventParamDto.text())));
        }

        if (eventParamDto.category() != null && !eventParamDto.category().isEmpty()) {
            paramFilter.and(event.category.id.in(eventParamDto.category()));
        }

        if (eventParamDto.paid() != null) {
            paramFilter.and(event.paid.eq(eventParamDto.paid()));
        }

        LocalDateTime start = eventParamDto.rangeStart() != null ? eventParamDto.rangeStart() : LocalDateTime.now();
        paramFilter.and(event.eventDate.goe(start));

        if (eventParamDto.rangeEnd() != null) {
            paramFilter.and(event.eventDate.loe(eventParamDto.rangeEnd()));
        }

        paramFilter.and(event.state.eq(EventState.PUBLISHED));

        if (eventParamDto.onlyAvailable()) {
            paramFilter.and(event.participantLimit.eq(0)
                    .or(event.participantLimit.gt(event.participantConfirmed)
                    ));
        }

        Sort sortEventDate = Sort.unsorted();
        if (eventParamDto.sort() != null && eventParamDto.sort().equalsIgnoreCase("EVENT_DATE")) {
            sortEventDate = Sort.by("eventDate").ascending();
        }

        Pageable pageable = PageRequest.of(eventParamDto.from() / eventParamDto.size(),
                eventParamDto.size(), sortEventDate);

        List<Event> events = eventRepository.findAll(paramFilter, pageable).getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream().map(Event::getId).toList();

        Map<Long, Long> confirmedRequestsMap = requestClient.findAllConfirmedRequestsByEventId(eventIds).stream()
                .collect(Collectors.toMap(ConfirmedRequestCount::eventId, ConfirmedRequestCount::count));
        Map<Long, Double> ratings = getRatingsMap(events);

        List<EventShortDto> shortsDto = events.stream()
                .map(eventMapper::toShortDto)
                .peek(shortDto -> {
                    shortDto.setConfirmedRequests(confirmedRequestsMap.getOrDefault(shortDto.getId(), 0L));
                    shortDto.setRating(ratings.getOrDefault(shortDto.getId(), 0D));
                })
                .toList();

        if (eventParamDto.sort() != null && eventParamDto.sort().equalsIgnoreCase("RATING")) {
            shortsDto.sort(Comparator.comparing(EventShortDto::getRating).reversed());
        }

        log.info("Получен список запросов по указанным фильтрам");

        return shortsDto;
    }


    @Override
    public EventFullDto getEventByIdPublic(Long eventId, Long userId) {
        Instant instant = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        collectorClient.collectUserAction(
                eventId,
                userId,
                "ACTION_VIEW",
                Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build());

        Event event = getEventOrThrow(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }

        EventFullDto fullDto = eventMapper.toFullDto(event);
        Double rating = getRatingsMap(List.of(event)).getOrDefault(eventId, 0D);

        fullDto.setConfirmedRequests(requestClient.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED));
        fullDto.setRating(rating);

        log.info("Получено событие с id = {}, rating: {}/{}", eventId, fullDto.getRating(), rating);

        return fullDto;
    }


    private UserShortDto getUserOrThrow(Long userId) {
        List<UserShortDto> userShortDtos = userClient.getUsersByIds(List.of(userId));

        if (userShortDtos.isEmpty()) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        return userShortDtos.get(0);
    }


    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }


    public Map<Long, Double> getRatingsMap(List<Event> events) {
        try {
            List<Long> eventIds = events.stream()
                    .map(event -> event.getId())
                    .toList();

            log.info("Try to get ratings for events: {}", eventIds);
            return analyzerClient.getInteractionsCount(eventIds)
                    .collect(Collectors.toMap(
                            recommendedEventProto -> recommendedEventProto.getEventId(),
                            recommendedEventProto -> recommendedEventProto.getScore()
            ));
        } catch (Exception e) {
            log.warn("Не удалось получить статистику просмотров: {}", e.getMessage());
            return Map.of();
        }
    }


    @Override
    public List<EventFullDto> searchEventsAdmin(AdminEventSearchFilter filter) {
        log.info("Search events with filters: {}", filter);

        if (filter.rangeStart() != null && filter.rangeEnd() != null &&
                filter.rangeStart().isAfter(filter.rangeEnd())) {
            throw new ValidationException("rangeEnd не может быть раньше rangeStart");
        }

        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder();

        if (filter.users() != null && !filter.users().isEmpty()) {
            predicate.and(event.initiatorId.in(filter.users()));
        }

        if (filter.states() != null && !filter.states().isEmpty()) {
            predicate.and(event.state.in(filter.states()));
        }

        if (filter.categories() != null && !filter.categories().isEmpty()) {
            predicate.and(event.category.id.in(filter.categories()));
        }

        if (filter.rangeStart() != null) {
            predicate.and(event.eventDate.goe(filter.rangeStart()));
        }

        if (filter.rangeEnd() != null) {
            predicate.and(event.eventDate.loe(filter.rangeEnd()));
        }


        Pageable pageable = PageRequest.of(filter.from() / filter.size(), filter.size());

        List<Event> events = eventRepository.findAll(predicate, pageable).getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream().map(Event::getId).toList();

        Map<Long, Long> confirmedRequestsMap = requestClient
                .findAllConfirmedRequestsByEventId(eventIds)
                .stream()
                .collect(Collectors.toMap(
                        ConfirmedRequestCount::eventId,
                        ConfirmedRequestCount::count
                ));

        Map<Long, Double> ratings = getRatingsMap(events);

        return events.stream()
                .map(eventMapper::toFullDto)
                .peek(fullDto -> {
                    fullDto.setConfirmedRequests(
                            confirmedRequestsMap.getOrDefault(fullDto.getId(), 0L));
                    fullDto.setRating(
                            ratings.getOrDefault(fullDto.getId(), 0D));
                })
                .toList();
    }


    @Override
    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto) {
        log.info("Update event with ID: {}", eventId);

        Event event = existsEvent(eventId);

        if (dto.eventDate() != null && dto.eventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Дата события должна быть не раньше, чем через час");
        }

        if (dto.annotation() != null) {
            event.setAnnotation(dto.annotation());
        }

        if (dto.description() != null) {
            event.setDescription(dto.description());
        }

        if (dto.eventDate() != null) {
            event.setEventDate(dto.eventDate());
        }

        if (dto.paid() != null) {
            event.setPaid(dto.paid());
        }

        if (dto.participantLimit() != null) {
            event.setParticipantLimit(dto.participantLimit());
        }

        if (dto.requestModeration() != null) {
            event.setRequestModeration(dto.requestModeration());
        }

        if (dto.title() != null) {
            event.setTitle(dto.title());
        }

        if (dto.location() != null) {
            event.setLocation(new Location(dto.location().getLat(), dto.location().getLon()));
        }

        if (dto.category() != null) {
            Category category = categoryRepository.findById(dto.category())
                    .orElseThrow(() -> new NotFoundException("Category with id= " + dto.category() + " was not found"));
            event.setCategory(category);
        }

        if (dto.stateAction() != null) {
            switch (dto.stateAction()) {
                case PUBLISH_EVENT -> {
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException(
                                "An event cannot be published unless it is in the required status (PENDING): "
                                        + event.getState());
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    log.info("Event с id={} успешно опубликовано", eventId);
                }
                case REJECT_EVENT -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Cannot publish the event because " +
                                "it's not in the right state: PUBLISHED");
                    }
                    event.setState(EventState.CANCELED);
                    log.info("Event с id={} отклонено", eventId);
                }
            }
        }

        Event updated = eventRepository.save(event);
        log.info("Event c id={} успешно обновлено", updated.getId());

        return eventMapper.toFullDto(updated);
    }


    @Override
    public Event existsEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }


    @Override
    public List<EventFullDto> getEventsByIds(List<Long> eventIds) {
        return eventRepository.findAllById(eventIds).stream().map(eventMapper::toFullDto).toList();
    }


    @Override
    public void updateEventParticipantConfirmedCnt(Long eventId, Long cnt) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " does not exisit.")
        );
        event.setParticipantConfirmed(cnt);
        Event eventUpdated = eventRepository.save(event);
        log.info("Updated event: {}", eventUpdated);
    }

    @Override
    public List<EventFullDto> getEventRecommendationsForUser(Long userId) {
        List<Long> recommendedEventIds = analyzerClient.getRecommendationsForUser(userId, 10)
                .map(event -> event.getEventId())
                .toList();

        return eventRepository.findAllById(recommendedEventIds).stream()
                .map(eventMapper::toFullDto)
                .toList();
    }

    @Override
    public void likeEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event " + eventId + "does not exist")
        );

        ParticipationRequestDto participation = requestClient.getEventRequests(userId, eventId).stream()
                .filter(part -> part.status().equals(ParticipationStatus.CONFIRMED))
                .findFirst().orElseThrow(
                        () -> new BadRequestException("User " + userId + "has not participate in the event " + eventId)
                );
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            Instant instant = LocalDateTime.now().toInstant(ZoneOffset.UTC);
            collectorClient.collectUserAction(eventId,
                    userId,
                    "ACTION_LIKE",
                    Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build());
        } else {
            throw new BadRequestException("Event "+ eventId + "has not happened yet");
        }

    }
}