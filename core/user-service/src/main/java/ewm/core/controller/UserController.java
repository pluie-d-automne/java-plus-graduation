package ewm.core.controller;

import ewm.core.dto.UserShortDto;
import ewm.core.client.UserClient;
import ewm.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@FeignClient(name = "user-client")
@RequestMapping("/user-client")
public class UserController implements UserClient {

    private final UserService userService;

    @Override
    @GetMapping
    public List<UserShortDto> getUsersByIds(@RequestParam(name="ids") List<Long> userIds) {
        log.info("Need to get UserShortDto list by ids: {}", userIds);
        List<UserShortDto> userShortDtos = userService.getUsersByIds(userIds);
        log.info("Return UserShortDto list: {}", userShortDtos);
        return userShortDtos;
    }
}
