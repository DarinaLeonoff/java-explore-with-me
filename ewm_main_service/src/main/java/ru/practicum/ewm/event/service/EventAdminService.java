package ru.practicum.ewm.event.service;

import jakarta.validation.Valid;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventAdminRequest;

import java.util.List;

public interface EventAdminService {
    List<EventFullDto> getEvent(List<Long> users, List<String> states, List<Integer> categories, String rangeStart,
            String rangeEnd, int from, int size);

    EventFullDto updateEvent(Long eventId, @Valid UpdateEventAdminRequest request);
}
