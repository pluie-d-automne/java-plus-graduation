package ewm.core.mapper;

import ewm.core.dto.UserShortDto;
import ewm.core.dto.UserDto;
import ewm.core.dto.UserPostDto;
import ewm.core.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);

    User userPostDtoToUser(UserPostDto userPostDto);

    UserShortDto userToUserShortDto(User user);
}
