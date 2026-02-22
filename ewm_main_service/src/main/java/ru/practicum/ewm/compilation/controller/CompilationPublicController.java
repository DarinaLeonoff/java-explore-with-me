package ru.practicum.ewm.compilation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
public class CompilationPublicController {
    @Autowired
    private CompilationService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CompilationDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return service.getPublicCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    CompilationDto getCompilationById(@PathVariable long compId) {
        return service.getPublicCompilationById(compId);
    }
}
