package ru.practicum.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class StatController {

    @PostMapping("/hit")
    public void hitNewStat(){
        System.out.println("HIT!");
    }
    @GetMapping("/stats")
    public void getStats(){
        System.out.println("Get stat");
    }
}
