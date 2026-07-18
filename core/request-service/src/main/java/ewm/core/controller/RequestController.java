package ewm.core.controller;

import ewm.core.dto.ParticipationRequestDto;
import ewm.core.service.ParticipationRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequestByUserId(@PathVariable Long userId) {
        return requestService.getRequestByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId,
                                              @RequestParam Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                              @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
