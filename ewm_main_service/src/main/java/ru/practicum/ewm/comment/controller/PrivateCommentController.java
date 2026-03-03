package ru.practicum.ewm.comment.controller;

//Зарегистрированные пользователи могут:
// 1. оставлять комментарии
// 2. редактировать свои комментарии
// 3. удалять свои комментарии

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentController {
    @Autowired
    private CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto postComment(@PathVariable Long eventId, @PathVariable Long userId,
            @RequestBody @Valid NewCommentDto dto) {
        return service.saveComment(eventId, userId, dto);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    CommentDto updateComment(@PathVariable Long eventId, @PathVariable Long userId, @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentDto dto) {
        return service.updateComment(eventId, userId, commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable Long commentId, @PathVariable Long userId, @PathVariable Long eventId) {
        service.authorRemoveComment(commentId, userId);
    }

}
