package ewm.core.compilation.dto;

import ewm.core.event.dto.EventShortDto;

import java.util.List;

public record CompilationDto(
        List<EventShortDto> events,
        Long id,
        Boolean pinned,
        String title) {
}
