package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventPrivateService {
    EventFullDto postNewEvent(NewEventDto dto, long userId);

    List<EventShortDto> getUserEvents(long userId, int from, int size);

    EventFullDto getUserEventById(long userId, long eventId);

    EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest request);
}
