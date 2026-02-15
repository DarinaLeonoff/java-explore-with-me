package ru.practicum.ewm.compilation.service;

import jakarta.validation.Valid;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;

public interface CompilationAdminService {
    CompilationDto createCompilation(NewCompilationDto dto);

    CompilationDto updateCompilation(long compId, @Valid UpdateCompilationDto dto);

    void removeCompilation(long compId);
}
