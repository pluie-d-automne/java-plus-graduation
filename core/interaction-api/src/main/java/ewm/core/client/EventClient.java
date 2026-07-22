package ewm.core.client;

import ewm.core.dto.EventFullDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "event-service", path = "/event-client")
public interface EventClient {
    @GetMapping
    List<EventFullDto> getEventsByIds(@RequestParam(name="ids") List<Long> eventIds);

    @PatchMapping("/ParticipantCnt/{eventId}")
    void updateEventParticipantConfirmedCnt(@PathVariable Long eventId,
                                                    @RequestParam(name="cnt") Long cnt);
}
