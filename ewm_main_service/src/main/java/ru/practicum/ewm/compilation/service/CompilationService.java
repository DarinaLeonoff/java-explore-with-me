package ru.practicum.ewm.compilation.service;

import jakarta.validation.Valid;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto adminCreateCompilation(NewCompilationDto dto);

    CompilationDto adminUpdateCompilation(long compId, @Valid UpdateCompilationDto dto);

    void adminRemoveCompilation(long compId);

    List<CompilationDto> getPublicCompilations(Boolean pinned, int from, int size);

    CompilationDto getPublicCompilationById(long compId);
}
