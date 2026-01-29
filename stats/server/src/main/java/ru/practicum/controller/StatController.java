package ru.practicum.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.service.StatService;

import java.util.List;

@RestController
public class StatController {

    @Autowired
    private StatService service;

    @PostMapping("/hit")
    public void hitNewStat(@Valid @RequestBody StatsRequestDto dto) {
        System.out.println("HIT!");
        service.hit(dto);
    }

    @GetMapping("/stats")
    public StatsResponseDto getStats() {
        System.out.println("Get stat");
        return service.getStats();
    }
}
