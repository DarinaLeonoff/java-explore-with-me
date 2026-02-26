package ru.practicum.ewm.comment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.AccessRightsException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.notFound.CommentNotFound;
import ru.practicum.ewm.exception.notFound.CompilationNotFound;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentMapper mapper;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CommentServiceImpl service;

    @Test
    void shouldCreateComment() {
        Event event = Event.builder().id(11L).state(EventState.PUBLISHED).build();
        User user = User.builder().id(22L).build();
        Comment comment = Comment.builder().user(user).event(event).text("New comment").created(LocalDateTime.now())
                .build();
        CommentDto dto = CommentDto.builder().event(event).user(user).text(comment.getText())
                .created(comment.getCreated()).build();

        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(mapper.mapNewCommentToComment(event, user, new NewCommentDto())).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(mapper.mapCommentToDto(comment)).thenReturn(dto);

        CommentDto result = service.saveComment(11L, 22L, new NewCommentDto());

        assertEquals(dto, result);
        verify(commentRepository).save(comment);
    }

    @Test
    void shouldThrowRightsException() {
        Event event = Event.builder().id(11L).state(EventState.PENDING).build();
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        assertThrows(AccessRightsException.class,
                () -> service.saveComment(1L, 1L, NewCommentDto.builder().text("text").build()));
    }

    @Test
    void shouldThrowWhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFound.class, () -> service.getCommentById(1L));
    }

    @Test
    void shouldThrowRightsExceptionForUser() {
        Event event = Event.builder().id(11L).build();
        User user = User.builder().id(22L).build();
        Comment old = Comment.builder().user(user).event(event).text("New comment").created(LocalDateTime.now())
                .build();

        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(old));

        assertThrows(AccessRightsException.class,
                () -> service.updateComment(11L, 1L, 1L, any(UpdateCommentDto.class)));
    }

    @Test
    void shouldThrowConflictExceptionForEvent() {
        Event event = Event.builder().id(11L).build();
        User user = User.builder().id(22L).build();
        Comment old = Comment.builder().user(user).event(event).text("New comment").created(LocalDateTime.now())
                .build();

        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(old));

        assertThrows(ConflictException.class, () -> service.updateComment(1L, 22L, 1L, any(UpdateCommentDto.class)));
    }

    @Test
    void shouldUpdateComment() {
        Event event = Event.builder().id(11L).build();
        User user = User.builder().id(22L).build();
        Comment old = Comment.builder().user(user).event(event).text("New comment").created(LocalDateTime.now())
                .build();

        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(old));
        when(commentRepository.save(old)).thenReturn(old);
        when(mapper.mapCommentToDto(any())).thenReturn(new CommentDto());

        CommentDto updated = service.updateComment(event.getId(), user.getId(), 1L,
                UpdateCommentDto.builder().text("new text").build());

        verify(commentRepository).save(old);
        assertEquals(old.getText(), "new text");
    }

    @Test
    void shouldReturnListByEventId() {
        Event event = Event.builder().id(11L).build();

        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(commentRepository.findAllByEventId(any())).thenReturn(List.of(new Comment(), new Comment()));
        when(mapper.mapCommentToDto(any(Comment.class))).thenReturn(new CommentDto());

        List<CommentDto> res = service.getAllCommentsByEventId(11L);

        assertEquals(2, res.size());
    }

    @Test
    void shouldReturnListByUserId() {
        User user = User.builder().id(11L).build();

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(commentRepository.findAllByUserId(any())).thenReturn(List.of(new Comment(), new Comment()));
        when(mapper.mapCommentToDto(any(Comment.class))).thenReturn(new CommentDto());

        List<CommentDto> res = service.getAllCommentsByUserId(11L);

        assertEquals(2, res.size());
    }

    @Test
    void shouldDelete() {
        service.adminRemoveComment(1L);

        verify(commentRepository).deleteById(1L);
    }

    @Test
    void shouldThrowRightsExceptionUserNotAuthor() {
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder().user(User.builder().id(2L).build()).event(new Event()).text("New comment")
                .created(LocalDateTime.now()).build();
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(comment));

        assertThrows(AccessRightsException.class, () -> service.authorRemoveComment(1L, 22L));
    }

    @Test
    void shouldDeleteIfAuthor() {
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder().user(user).event(new Event()).text("New comment")
                .created(LocalDateTime.now()).build();

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(comment));

        service.authorRemoveComment(1L, 1L);
        verify(commentRepository).deleteById(1L);
    }
}
