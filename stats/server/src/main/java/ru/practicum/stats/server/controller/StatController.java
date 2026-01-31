package ru.practicum.stats.server.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.stats.server.service.StatService;

@RestController
public class StatController {

    @Autowired
    private StatService service;

    @PostMapping("/hit")
    public void hitNewStat(@Valid @RequestBody StatsRequestDto dto) {
        service.hit(dto);
    }

    @GetMapping("/stats")
    public StatsResponseDto getStats() {
        return service.getStats();
    }
}
