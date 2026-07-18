package ewm.core.comments.mapper;

import ewm.core.comments.dto.CommentDto;
import ewm.core.comments.dto.PostCommentParam;
import ewm.core.comments.model.Comment;
import ewm.core.mapper.UserMapper;
import ewm.core.event.service.EventService;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, EventService.class})
public interface CommentMapper {
    CommentDto toCommentDto(Comment comment);

    Comment postToComment(PostCommentParam postCommentParam);

    List<CommentDto> toFullDtoList(List<Comment> comments);
}