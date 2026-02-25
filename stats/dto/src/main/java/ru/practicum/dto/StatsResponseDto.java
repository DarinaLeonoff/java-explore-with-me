package ru.practicum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Valid
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsResponseDto {
    @NotNull
    private String app, uri;

    @PositiveOrZero
    private Long hits;
}
