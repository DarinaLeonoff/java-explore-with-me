package ru.practicum.ewm.request.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.ewm.validationAnnotation.ConfirmOrRejectedRequest;
import ru.practicum.ewm.request.model.RequestState;

import java.util.List;

@Data
@Valid
public class EventRequestStatusUpdateRequest {
    @NotNull
    List<Long> requestIds;

    @NotNull
    @ConfirmOrRejectedRequest
    RequestState status;
}
