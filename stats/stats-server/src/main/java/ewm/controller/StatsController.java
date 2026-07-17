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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FeignClient(name = "stats-server")
public class StatsController implements StatClient {
    private final StatsService statsService;

    @Override
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody HitDto hitDto) {
        log.info("Запрошен hit: {}", hitDto);
        statsService.createHit(hitDto);
    }

    @Override
    @GetMapping("/stats")
    public List<StatsDto> getStats(@Valid @RequestParam(name="start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam(name="end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(name="uris") List<String> uris,
                                   @RequestParam(name="unique", required = false, defaultValue = "false") Boolean unique) {
        log.info("Запрошено получение статистики start: {}, end: {}, uris: {}, unique {}", start, end, uris, unique);
        ParamDto paramDto = new ParamDto(start, end, uris, unique);
        return statsService.getStats(paramDto);
    }
}
