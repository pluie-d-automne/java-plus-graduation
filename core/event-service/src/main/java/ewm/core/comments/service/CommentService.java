package ewm.core.comments.service;

import ewm.core.comments.dto.AdminCommentSearchFilter;
import ewm.core.comments.dto.CommentDto;

import ewm.core.comments.dto.CommentSearchParams;
import ewm.core.comments.dto.PostCommentParam;
import ewm.core.comments.dto.UpdateCommentParam;
import ewm.core.comments.dto.UpdateCommentStatusRequest;

import java.util.List;

public interface CommentService {
    CommentDto create(PostCommentParam postCommentParam);

    CommentDto update(UpdateCommentParam updCommentParam);

    void delete(Long userId, Long commentId);

    List<CommentDto> findAllByAuthor(Long userId);

    CommentDto findByIdAndAuthor(Long userId, Long commentId);

    List<CommentDto> findAllByEventAndAuthor(Long userId, Long eventId);

    List<CommentDto> getPublishedComments(CommentSearchParams params);

    CommentDto getPublishedComment(Long commentId);

    List<CommentDto> searchComments(AdminCommentSearchFilter filter);

    CommentDto findCommentById(Long commentId);

    CommentDto updateStatusComment(Long commentId, UpdateCommentStatusRequest status);

    void deleteComment(Long commentId);
}
