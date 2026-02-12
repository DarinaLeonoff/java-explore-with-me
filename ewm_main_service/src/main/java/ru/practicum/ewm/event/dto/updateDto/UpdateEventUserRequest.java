package ru.practicum.ewm.event.dto.updateDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Valid
public class UpdateEventUserRequest extends UpdateEventRequest {
    private StateUserAction stateAction;
}