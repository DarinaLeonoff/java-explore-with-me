package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventPrivateService;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    @Autowired
    private EventPrivateService eventPrivateService;

    //    todo privatePost
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto postNewEvent(@RequestBody @Valid NewEventDto dto, @PathVariable long userId) {
        log.info("post new event by user {}, {}", userId, dto);
        return eventPrivateService.postNewEvent(dto, userId);
    }


    //    todo privateGet
//    todo privateGet
//    todo privateGet
//    todo privatePatch
//    todo privatePatch
//    todo privatePatch
//    todo privateGetRequests
//    todo privatePost
//    todo privatePatch
}
