package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.service.SortType;

import java.util.List;

@RestController
@RequestMapping("/events")
@Validated
public class EventPublicController {
    @Autowired
    private EventService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventShortDto> getEventList(@RequestParam(required = false) String text,
            @RequestParam(required = false) List<Integer> categories, @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart, @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable, @RequestParam(required = false) SortType sort,
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10") @Min(value = 1) int size, HttpServletRequest request) {
        return service.getPublicEventList(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request.getRemoteAddr());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto getEvent(@PathVariable long id, HttpServletRequest request) throws InterruptedException {
        return service.getPublicEvent(id, request.getRemoteAddr());
    }

}
