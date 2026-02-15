package ru.practicum.ewm.compilation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compilations")
public class CompilationPublicController {
    //todo
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    void getCompilations() {
    }

    //todo
    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    void getCompilationById(@PathVariable long compId) {
    }
}
