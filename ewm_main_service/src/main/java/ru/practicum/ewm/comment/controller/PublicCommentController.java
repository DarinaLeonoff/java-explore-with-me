package ru.practicum.ewm.comment.controller;

//незарегистрированный пользователь может:
//1.просмотреть комментарии к определенному событию
//2.просмотреть комментарии пользователя
//3.просмотреть конкретный комментарий

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {
    @Autowired
    private CommentService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CommentDto> getEventComments(@PathVariable Long eventId) {
        return service.getAllCommentsByEventId(eventId);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    List<CommentDto> getUserComments(@PathVariable Long userId) {
        return service.getAllCommentsByUserId(userId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    CommentDto getComment(@PathVariable Long commentId) {
        return service.getCommentById(commentId);
    }
}
