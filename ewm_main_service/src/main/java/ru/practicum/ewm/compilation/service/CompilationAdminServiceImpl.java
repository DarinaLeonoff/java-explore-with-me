package ru.practicum.ewm.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.notFound.CompilationNotFound;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CompilationAdminServiceImpl implements CompilationAdminService {
    @Autowired
    private CompilationMapper mapper;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CompilationRepository compilationRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Compilation comp = mapper.mapNewCompilationToEntity(dto);
        comp.setEvents(getEvents(dto.getEvents()));
        return mapper.mapEntityToDto(compilationRepository.save(comp));
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationDto dto) {
        Compilation oldComp = compilationRepository.findById(compId).orElseThrow(() -> new CompilationNotFound(compId));
        Compilation updated = mapper.updateCompilation(oldComp, dto);
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            updated.setEvents(eventRepository.findAllById(dto.getEvents()));
        }
        return mapper.mapEntityToDto(compilationRepository.save(updated));
    }

    @Override
    public void removeCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    private List<Event> getEvents(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return eventRepository.findAllById(ids);
    }
}
