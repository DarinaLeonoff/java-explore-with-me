package ru.practicum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Valid
@Getter
@Setter
@AllArgsConstructor
@Builder
public class StatsRequestDto {
    @NotNull
    private String app, uri;

    @NotNull
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$",
            message = "Принимаются только корректные адреса от 0.0.0.0 до 255.255.255.255")
    private String ip;

    @NotNull
    @PastOrPresent
    private LocalDateTime timestamp;
}
