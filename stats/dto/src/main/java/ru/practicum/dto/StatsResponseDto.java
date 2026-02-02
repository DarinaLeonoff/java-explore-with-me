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
    private int hits;

    public StatsResponseDto(String app, String uri, long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = (int) hits;
    }
}
