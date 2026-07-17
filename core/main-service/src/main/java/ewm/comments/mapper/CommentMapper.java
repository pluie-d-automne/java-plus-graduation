package ewm.comments.mapper;

import ewm.comments.dto.CommentDto;
import ewm.comments.dto.PostCommentParam;
import ewm.comments.model.Comment;
import ewm.core.mapper.UserMapper;
import ewm.event.service.EventService;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, EventService.class})
public interface CommentMapper {
    CommentDto toCommentDto(Comment comment);

    Comment postToComment(PostCommentParam postCommentParam);

    List<CommentDto> toFullDtoList(List<Comment> comments);
}