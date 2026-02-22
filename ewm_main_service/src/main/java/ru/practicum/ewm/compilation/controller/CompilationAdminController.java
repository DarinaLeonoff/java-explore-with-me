package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    @Autowired
    private CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto dto) {
        return service.adminCreateCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeCompilation(@PathVariable long compId) {
        service.adminRemoveCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    CompilationDto updateCompilation(@PathVariable long compId, @RequestBody @Valid UpdateCompilationDto dto) {
        return service.adminUpdateCompilation(compId, dto);
    }
}
