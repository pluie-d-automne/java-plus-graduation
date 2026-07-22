package ewm.core.service;

import ewm.core.dto.AdminCommentSearchFilter;
import ewm.core.dto.CommentDto;

import ewm.core.dto.CommentSearchParams;
import ewm.core.dto.PostCommentParam;
import ewm.core.dto.UpdateCommentParam;
import ewm.core.dto.UpdateCommentStatusRequest;

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
