package ewm.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ewm.core.model.CommentStatus;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,

        String comment,

        CommentStatus status,

        EventPreviewDto event,

        UserShortDto author,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime editedOn
) {
}
