package ru.practicum.ewm.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    @Autowired
    private RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto makeNewRequest(@PathVariable long userId, @RequestParam(required = true) long eventId) {

        return service.makeRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<ParticipationRequestDto> getRequest(@PathVariable long userId) {

        return service.getUserRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    ParticipationRequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {

        return service.cancelRequest(userId, requestId);
    }
}
