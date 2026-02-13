package ru.practicum.ewm.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "event_id"}
        )
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    private RequestState status;

    private LocalDateTime created;
}
