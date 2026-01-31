package ru.practicum.stats.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app")
    private String app;
    @Column(name = "uri")
    private String uri;
    @Column(name = "ip")
    private String ip;
    @Column(name = "created")
    private LocalDateTime created;
}
