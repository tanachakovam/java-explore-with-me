package ru.practicum.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.model.Comment;


import java.util.Collection;
import java.util.List;


@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author", source = "comment.author.name")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "author", source = "comment.author.name")
    List<CommentDto> toCommentDtoCollection(Collection<Comment> comment);

}

