package ru.practicum.ewm.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring", imports = {java.time.LocalDateTime.class})
public interface CommentMapper {

    CommentDto mapCommentToDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", source = "event")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    Comment mapNewCommentToComment(Event event, User user, NewCommentDto dto);

}
