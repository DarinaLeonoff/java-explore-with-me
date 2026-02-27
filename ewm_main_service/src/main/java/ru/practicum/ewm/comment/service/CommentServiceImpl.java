package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.AccessRightsException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.notFound.CommentNotFound;
import ru.practicum.ewm.exception.notFound.EventNotFound;
import ru.practicum.ewm.exception.notFound.UserNotFound;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private final CommentMapper mapper;

    //private
    @Override
    public CommentDto saveComment(Long eventId, Long userId, NewCommentDto dto) {
        Event event = getEvent(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new AccessRightsException("Нельзя написать комментарий к неопубликованному событию.");
        }
        User user = getUser(userId);
        Comment comment = commentRepository.save(mapper.mapNewCommentToComment(event, user, dto));
        return mapper.mapCommentToDto(comment);
    }

    //private
    @Override
    public CommentDto updateComment(Long eventId, Long userId, Long commentId, UpdateCommentDto dto) {
        Event event = getEvent(eventId);
        User user = getUser(userId);
        Comment old = getComment(commentId);
        if (!Objects.equals(old.getUser().getId(), userId)) {
            throw new AccessRightsException("This user is not author of comment.");
        }
        if (!Objects.equals(old.getEvent().getId(), eventId)) {
            throw new ConflictException("This comment is not about event with id " + eventId + ".");
        }
        Comment updated = commentRepository.save(update(old, dto));

        return mapper.mapCommentToDto(updated);
    }

    //public
    @Override
    public CommentDto getCommentById(Long commentId) {
        getComment(commentId);
        return mapper.mapCommentToDto(getComment(commentId));
    }

    //public
    @Override
    public List<CommentDto> getAllCommentsByEventId(Long eventId) {
        Event event = getEvent(eventId);
        List<Comment> commentList = commentRepository.findAllByEventId(eventId);
        return commentList.stream().map(mapper::mapCommentToDto).toList();
    }

    //public
    @Override
    public List<CommentDto> getAllCommentsByUserId(Long userId) {
        User user = getUser(userId);
        List<Comment> commentList = commentRepository.findAllByUserId(userId);
        return commentList.stream().map(mapper::mapCommentToDto).toList();
    }

    //admin
    @Override
    public void adminRemoveComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    //private
    @Override
    public void authorRemoveComment(Long commentId, Long userId) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessRightsException("Пользователь не являющийся автором не может удалить комментарий.");
        }
        commentRepository.deleteById(commentId);
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFound(id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFound(id));
    }

    private Comment update(Comment old, UpdateCommentDto dto) {
        old.setText(dto.getText());
        old.setEdited(LocalDateTime.now());
        return old;
    }
}
