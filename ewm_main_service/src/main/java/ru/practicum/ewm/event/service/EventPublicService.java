package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

public interface EventPublicService {
    List<EventShortDto> getEventList(String text, List<Integer> categories, Boolean paid, String rangeStart,
            String rangeEnd, Boolean onlyAvailable, SortType sort, int from, int size, String ip);

    EventFullDto getEvent(long id, String ip) throws InterruptedException;
}
