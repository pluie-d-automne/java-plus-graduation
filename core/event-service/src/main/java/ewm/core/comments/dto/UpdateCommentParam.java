package ewm.core.comments.dto;

public record UpdateCommentParam(
        Long author,
        Long commentId,
        String comment
) {
}
