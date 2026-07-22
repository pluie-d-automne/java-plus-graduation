package ewm.core.event.service;

import ewm.core.event.dto.AdminEventSearchFilter;
import ewm.core.dto.EventFullDto;
import ewm.core.event.dto.EventShortDto;
import ewm.core.event.dto.NewEventDto;
import ewm.core.event.dto.PublicEventParamDto;
import ewm.core.event.dto.UpdateEventAdminRequest;
import ewm.core.event.dto.UpdateEventUserRequest;
import ewm.core.event.model.Event;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface EventService {

    List<EventShortDto> getEventsPrivate(Long userId, Integer from, Integer size);

    EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByIdPrivate(Long userId, Long eventId, String url);

    EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    List<EventShortDto> getEventsPublic(PublicEventParamDto paramDto, HttpServletRequest request);

    EventFullDto getEventByIdPublic(Long id, HttpServletRequest request);

    List<EventFullDto> searchEventsAdmin(AdminEventSearchFilter filter);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto);

    Map<Long, Long> getViewsMap(List<Event> events, boolean unique);

    Event existsEvent(Long eventId);

    List<EventFullDto> getEventsByIds(List<Long> eventIds);

    void updateEventParticipantConfirmedCnt(Long eventId, Long cnt);
}