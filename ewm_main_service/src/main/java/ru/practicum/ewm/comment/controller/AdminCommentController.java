package ru.practicum.ewm.comment.controller;
//Администратор имеет право:
//1. удалять комментарии любого пользователя

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@RequestMapping("/admin/events/{eventId}/comments")
public class AdminCommentController {
    @Autowired
    private CommentService service;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable Long commentId) {
        service.adminRemoveComment(commentId);
    }

}
