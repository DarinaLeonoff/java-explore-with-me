package ru.practicum.ewm.event.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Valid
@Embeddable
public class Location {
    @NotNull
    private Double lat; //широта
    @NotNull
    private Double lon; //долгота
}
