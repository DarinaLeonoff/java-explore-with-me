package ru.practicum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Valid
@Getter
@Setter
@AllArgsConstructor
@Builder
public class StatsResponseDto {
    @NotNull
    private String app, uri;

    @PositiveOrZero
    private int hits;
}
