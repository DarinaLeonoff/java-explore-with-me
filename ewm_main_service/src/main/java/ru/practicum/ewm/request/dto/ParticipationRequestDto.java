package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.request.model.RequestState;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private Long id;

    private long requester;

    private long event;

    private RequestState status;

    @JsonFormat(pattern = Constants.DATE_FORMATE)
    private LocalDateTime created;
}
