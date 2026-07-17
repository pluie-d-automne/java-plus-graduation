package ewm.service;

import ewm.HitDto;
import ewm.ParamDto;
import ewm.StatsDto;
import ewm.exception.ValidationException;
import ewm.mapper.EndpointHitMapper;
import ewm.model.EndpointHit;
import ewm.repository.StatsRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    @Transactional
    public void createHit(HitDto hitDto) {
        EndpointHit hit = endpointHitMapper.mapToEndpointHit(hitDto);

        EndpointHit saveHit = statsRepository.save(hit);

        log.info("Сохранена запись о посещении - URI: {} (ID: {}, ts: {})", saveHit.getUri(), saveHit.getId(), saveHit.getTimestamp());
    }

    public List<StatsDto> getStats(ParamDto paramDto) {
        if (paramDto.start().isAfter(paramDto.end())) {
            throw new ValidationException("Дата и время начала " + paramDto.start() +
                    " не должны быть позже даты и времени конца " + paramDto.end());
        }

        List<StatsDto> result;
        boolean isUnique = paramDto.unique() != null && paramDto.unique();

        if (paramDto.uris() == null || paramDto.uris().isEmpty()) {
            result = isUnique
                    ? statsRepository.getStatsUnique(paramDto.start(), paramDto.end())
                    : statsRepository.getStats(paramDto.start(), paramDto.end());
        } else {
            result = isUnique
                    ? statsRepository.getStatsByUriUnique(paramDto.start(), paramDto.end(), paramDto.uris())
                    : statsRepository.getStatsByUri(paramDto.start(), paramDto.end(), paramDto.uris());
        }
        log.info("Result stats: {}", result);
        return result;
    }
}
