package ewm.core.dto;

public record PostCommentParam(
        Long author,
        Long event,
        String comment
) {
}
