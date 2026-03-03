package ru.practicum.ewm.comment.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CommentMapperTest {
    private CommentMapperImpl mapper = new CommentMapperImpl();

    private Comment comment;

    @BeforeEach
    void setup() {
        comment = Comment.builder().id(1L).event(new Event()).user(new User()).text("text").created(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldMapAllFieldCorrect() {
        CommentDto dto = mapper.mapCommentToDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getUser().getId(), dto.getUser().getId());
        assertEquals(comment.getEvent().getId(), dto.getEvent().getId());
        assertEquals(comment.getText(), dto.getText());
    }

    @Test
    void shouldMakeCommentFromNewCommentDto() {
        NewCommentDto dto = NewCommentDto.builder().text("new comment").build();

        Comment newComment = mapper.mapNewCommentToComment(Event.builder().id(21L).build(),
                User.builder().id(12L).build(), dto);

        assertEquals(21L, newComment.getEvent().getId());
        assertEquals(12L, newComment.getUser().getId());
        assertEquals(dto.getText(), newComment.getText());
    }
}
