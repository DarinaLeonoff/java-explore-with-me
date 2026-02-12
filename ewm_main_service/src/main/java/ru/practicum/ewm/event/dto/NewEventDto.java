package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.event.model.Location;

import java.time.LocalDateTime;

@Data
@Valid
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private int category;
    @NotNull
    @Size(min = 20, max = 7000)
    private String description;
    @NotNull
    @JsonFormat(pattern = Constants.DATE_FORMATE)
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private boolean paid = false;
    private int participantLimit = 0;
    private boolean requestModeration = true;
    @NotNull
    @Size(min = 3, max = 120)
    private String title;
}
