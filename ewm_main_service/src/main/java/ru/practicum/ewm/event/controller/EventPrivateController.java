package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    @Autowired
    private EventService eventService;
    @Autowired
    private RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto postNewEvent(@RequestBody @Valid NewEventDto dto, @PathVariable long userId) {
        log.info("post new event by user {}", userId);
        return eventService.userPostNewEvent(dto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
        //список событий добавленных юзером
    List<EventShortDto> getListOfUserEvents(@PathVariable long userId, @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get events posted by user {}", userId);
        return eventService.userGetUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
        //событие из числа добавленных юзером
    EventFullDto getUserEventById(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.userGetUserEventById(userId, eventId);
    }


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto updateUserEvent(@PathVariable long userId, @PathVariable long eventId,
            @RequestBody @Valid UpdateEventUserRequest request) {
        return eventService.userUpdateUserEvent(userId, eventId, request);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    Map<String, List<ParticipationRequestDto>> acceptRequest(@PathVariable long userId, @PathVariable long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest request) throws AccessException {
        return requestService.acceptRequest(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    List<ParticipationRequestDto> getEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }
}
