package ru.practicum.ewm.comment.dto;

import lombok.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CommentDto {
    private Long id;

    private Event event;

    private User user;

    private String text;

    private LocalDateTime created;

    private LocalDateTime edited;
}
