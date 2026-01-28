package ru.practicum.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.StatsRequestDto;

@RestController
public class StatController {

    @PostMapping("/hit")
    public void hitNewStat(@Valid @RequestBody StatsRequestDto dto) {
        System.out.println("HIT!");
        System.out.println(dto.toString());
    }

    @GetMapping("/stats")
    public void getStats() {
        System.out.println("Get stat");
    }
}
