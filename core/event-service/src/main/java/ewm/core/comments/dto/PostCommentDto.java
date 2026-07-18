package ewm.core.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCommentDto(
        @NotBlank
        @Size(min = 20, max = 10000)
        String comment
) {}
