package ewm.core.comments.controller;

import ewm.core.comments.dto.CommentDto;
import ewm.core.comments.dto.CommentSearchParams;
import ewm.core.comments.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(@Valid CommentSearchParams params) {
        return commentService.getPublishedComments(params);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long commentId) {
        return commentService.getPublishedComment(commentId);
    }
}