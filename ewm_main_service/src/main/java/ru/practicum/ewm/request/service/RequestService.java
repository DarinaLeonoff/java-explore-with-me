package ru.practicum.ewm.request.service;

import org.springframework.expression.AccessException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {
    ParticipationRequestDto makeRequest(long userId, long eventId);

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);


    Map<String, List<ParticipationRequestDto>> acceptRequest(long userId, long eventId,
            EventRequestStatusUpdateRequest request) throws AccessException;
}
