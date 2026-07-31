package ewm.core.event.controller;

import ewm.core.dto.EventFullDto;
import ewm.core.event.dto.EventShortDto;
import ewm.core.event.dto.PublicEventParamDto;
import ewm.core.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getPublicEvents(@Valid PublicEventParamDto param) {
        log.info("GET /event: {}", param);
        return eventService.getEventsPublic(param);
    }

    @GetMapping("/{id}")
    public EventFullDto getPublicEventById(@PathVariable Long id,
                                           @RequestHeader("X-EWM-USER-ID") Long userId) {
        log.info("GET /event/{id}: id={}", id);
        return eventService.getEventByIdPublic(id, userId);
    }

    @GetMapping("/recommendations")
    public List<EventFullDto> getEventRecommendationsForUser(@RequestHeader("X-EWM-USER-ID") Long userId) {
        log.info("GET recommendations for user with id={}", userId);
        return eventService.getEventRecommendationsForUser(userId);
    }

    @PutMapping("/{eventId}/like")
    public void likeEvent(@RequestHeader("X-EWM-USER-ID") Long userId,
                          @PathVariable Long eventId) {
        log.info("User {} wants to like event with id={}", userId, eventId);
        eventService.likeEvent(userId, eventId);
    }
}
