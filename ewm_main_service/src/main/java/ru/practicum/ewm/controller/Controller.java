package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.client.StatsClient;

@Slf4j
@RestController
@RequestMapping("/check")
public class Controller {

    @Autowired
    StatsClient statsClient;

    @PostMapping
    public void check() {
        log.info("Start hitting stats");
        statsClient.hit("ewm-main-service", "192.163.0.1");
    }
}
