package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", ignore = true)
    Compilation mapNewCompilationToEntity(NewCompilationDto dto);

    CompilationDto mapEntityToDto(Compilation compilation);

    default Compilation updateCompilation(Compilation old, UpdateCompilationDto dto) {
        if (dto.getTitle() != null) {
            old.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            old.setPinned(dto.getPinned());
        }
        return old;
    }
}
