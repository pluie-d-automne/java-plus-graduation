package ewm.core.event.controller;

import ewm.core.client.EventClient;
import ewm.core.dto.EventFullDto;
import ewm.core.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@FeignClient(name = "event-client")
@RequestMapping("/event-client")
public class EventClientController implements EventClient {
    private final EventService eventService;

    @Override
    @GetMapping
    public List<EventFullDto> getEventsByIds(@RequestParam(name="ids") List<Long> eventIds) {
        log.info("Need to get EventFullDto list by Ids: {}", eventIds);
        List<EventFullDto> eventFullDtos = eventService.getEventsByIds(eventIds);
        log.info("Return list of EventFullDto: {}", eventFullDtos);
        return eventFullDtos;
    }

    @Override
    @PatchMapping("/ParticipantCnt/{eventId}")
    public void updateEventParticipantConfirmedCnt(@PathVariable Long eventId,
                                                           @RequestParam(name="cnt") Long cnt) {

    }
}
