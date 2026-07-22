package ewm.core.dto;

public record UpdateCommentParam(
        Long author,
        Long commentId,
        String comment
) {
}
