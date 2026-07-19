package ewm.core.dto;

import ewm.core.model.CommentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCommentStatusRequest(
        @NotNull
        CommentStatus status
) {
}