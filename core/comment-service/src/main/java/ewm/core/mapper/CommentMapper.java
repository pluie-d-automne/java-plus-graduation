package ewm.core.mapper;

import ewm.core.dto.CommentDto;
import ewm.core.dto.PostCommentParam;
import ewm.core.model.Comment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class})
public interface CommentMapper {
    CommentDto toCommentDto(Comment comment);

    Comment postToComment(PostCommentParam postCommentParam);

    List<CommentDto> toFullDtoList(List<Comment> comments);
}