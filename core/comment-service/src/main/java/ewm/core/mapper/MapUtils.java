package ewm.core.mapper;

import ewm.core.dto.EventPreviewDto;
import ewm.core.dto.UserShortDto;
import org.springframework.stereotype.Component;

@Component
public class MapUtils {
    public UserShortDto mapIdToUserShortDto(Long id) {
        return new UserShortDto(id, null);
    }

    public EventPreviewDto mapIdToEventPreviewDto(Long id) {
        return new EventPreviewDto(id, null, null, null, null, null);
    }
}
