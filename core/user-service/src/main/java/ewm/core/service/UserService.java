package ewm.core.service;

import ewm.core.dto.UserShortDto;
import ewm.core.dto.AdminUserParam;
import ewm.core.dto.UserDto;
import ewm.core.dto.UserPostDto;
import ewm.core.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserPostDto userPostDto);

    List<UserDto> findAll(AdminUserParam params);

    void delete(Long userId);

    User findById(Long userId);

    List<UserShortDto> getUsersByIds(List<Long> userIds);
}
