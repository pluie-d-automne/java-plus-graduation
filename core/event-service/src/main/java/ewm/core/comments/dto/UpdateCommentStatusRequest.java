package ewm.core.comments.dto;

import ewm.core.comments.model.CommentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCommentStatusRequest(
        @NotNull
        CommentStatus status
) {
}