package ewm.core.event.mapper;

import ewm.core.dto.UserShortDto;
import org.springframework.stereotype.Component;

@Component
public class MapUtils {
    public UserShortDto mapIdToUserShortDto(Long id) {
        return new UserShortDto(id, null);
    }

    public Long mapUserShortDtoToId(UserShortDto user) {
        return user.id();
    }
}
