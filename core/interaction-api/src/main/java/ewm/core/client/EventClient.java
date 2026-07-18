package ewm.core.client;

import ewm.core.dto.EventFullDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "event-client")
public interface EventClient {
    @GetMapping
    List<EventFullDto> getEventsByIds(@RequestParam(name="ids") List<Long> eventIds);
}
