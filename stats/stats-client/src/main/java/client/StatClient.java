package client;

import ewm.HitDto;
import ewm.ParamDto;
import ewm.StatsDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stats-server")
public interface StatClient {
    @PostMapping("/hit")
    public void createHit(@Valid @RequestBody HitDto hitDto);

    @GetMapping("/stats")
    public List<StatsDto> getStats(@Valid @RequestBody ParamDto paramDto);

    @GetMapping("/stats")
    public List<StatsDto> getStats(@Valid @RequestParam(name="start") LocalDateTime start,
                                   @RequestParam(name="end") LocalDateTime end,
                                   @RequestParam(name="uris") List<String> uris);
}
