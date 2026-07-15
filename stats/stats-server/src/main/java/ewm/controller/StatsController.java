package ewm.controller;

import client.StatClient;
import ewm.HitDto;
import ewm.ParamDto;
import ewm.StatsDto;
import ewm.service.StatsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FeignClient(name = "stats-server")
public class StatsController implements StatClient {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody HitDto hitDto) {
        log.info("Запрошен hit: {}", hitDto);
        statsService.createHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@Valid @RequestBody ParamDto paramDto) {
        log.info("Запрошено получение статистики: {}", paramDto);
        return statsService.getStats(paramDto);
    }
}
