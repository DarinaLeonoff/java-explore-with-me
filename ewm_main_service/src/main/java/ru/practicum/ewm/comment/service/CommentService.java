package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto saveComment(Long eventId, Long userId, NewCommentDto dto);

    CommentDto updateComment(Long eventId, Long userId, Long commentId, UpdateCommentDto dto);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getAllCommentsByEventId(Long eventId);

    List<CommentDto> getAllCommentsByUserId(Long userId);

    void authorRemoveComment(Long commentId, Long userId);

    void adminRemoveComment(Long commentId);
}
