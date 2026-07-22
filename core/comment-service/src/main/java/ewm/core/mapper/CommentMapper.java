package ewm.core.mapper;

import ewm.core.dto.CommentDto;
import ewm.core.dto.PostCommentParam;
import ewm.core.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {MapUtils.class})
public interface CommentMapper {
    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "event", source = "eventId")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "authorId", source = "author")
    @Mapping(target = "eventId", source = "event")
    Comment postToComment(PostCommentParam postCommentParam);

    List<CommentDto> toFullDtoList(List<Comment> comments);
}