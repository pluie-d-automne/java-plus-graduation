package ewm.core.mapper;

import ewm.core.dto.UserShortDto;

public class UserMapper {
    public UserShortDto mapIdToUserShortDto(Long id) {
        return new UserShortDto(id, null);
    }

    public Long mapUserShortDto(UserShortDto user) {
        return user.id();
    }
}
