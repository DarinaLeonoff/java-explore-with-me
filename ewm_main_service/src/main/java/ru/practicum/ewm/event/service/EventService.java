package ru.practicum.ewm.event.service;

import jakarta.validation.Valid;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    //admin
    List<EventFullDto> adminGetEvent(List<Long> users, List<String> states, List<Integer> categories, String rangeStart,
            String rangeEnd, int from, int size);

    EventFullDto adminUpdateEvent(Long eventId, @Valid UpdateEventAdminRequest request);

    //private
    EventFullDto userPostNewEvent(NewEventDto dto, long userId);

    List<EventShortDto> userGetUserEvents(long userId, int from, int size);

    EventFullDto userGetUserEventById(long userId, long eventId);

    EventFullDto userUpdateUserEvent(long userId, long eventId, UpdateEventUserRequest request);

    //public
    List<EventShortDto> getPublicEventList(String text, List<Integer> categories, Boolean paid, String rangeStart,
            String rangeEnd, Boolean onlyAvailable, SortType sort, int from, int size, String ip);

    EventFullDto getPublicEvent(long id, String ip) throws InterruptedException;
}
