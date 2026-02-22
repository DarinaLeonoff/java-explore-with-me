package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class EventAdminController {
    @Autowired
    private EventService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventFullDto> getEventsForAdmin(@RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Integer> categories, @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd, @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return service.adminGetEvent(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto adminUpdateEvent(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequest request) {
        return service.adminUpdateEvent(eventId, request);
    }

}
