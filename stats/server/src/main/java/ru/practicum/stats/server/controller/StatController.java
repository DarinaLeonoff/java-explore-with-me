package ru.practicum.stats.server.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.stats.server.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class StatController {

    @Autowired
    private StatService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hitNewStat(@Valid @RequestBody StatsRequestDto dto) {
        service.hit(dto);
    }

    @GetMapping("/stats")
    public List<StatsResponseDto> getStats(@RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start, @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end, @RequestParam(required = false) List<String> uris, @RequestParam(required = false) Boolean unique) {
        return service.getStats(start, end, uris, unique);
    }
}
