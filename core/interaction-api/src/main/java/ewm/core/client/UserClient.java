package ewm.core.client;

import ewm.core.dto.UserShortDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", path = "/users/user-client")
public interface UserClient {
    @GetMapping
    List<UserShortDto> getUsersByIds(@RequestParam(name="ids") List<Long> userIds);
}
