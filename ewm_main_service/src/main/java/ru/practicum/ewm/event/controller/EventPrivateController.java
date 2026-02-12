package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventPrivateService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    @Autowired
    private EventPrivateService eventPrivateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto postNewEvent(@RequestBody @Valid NewEventDto dto, @PathVariable long userId) {
        log.info("post new event by user {}", userId);
        return eventPrivateService.postNewEvent(dto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
        //список событий добавленных юзером
    List<EventShortDto> getListOfUserEvents(@PathVariable long userId, @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get events posted by user {}", userId);
        return eventPrivateService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
        //событие из числа добавленных юзером
    EventFullDto getUserEventById(@PathVariable long userId, @PathVariable long eventId) {
        return eventPrivateService.getUserEventById(userId, eventId);
    }


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto updateUserEvent(@PathVariable long userId, @PathVariable long eventId,
            @RequestBody @Valid UpdateEventUserRequest request) {
        return eventPrivateService.updateUserEvent(userId, eventId, request);
    }

//    todo privatePatchRequest
//    todo privateGetRequests
}
