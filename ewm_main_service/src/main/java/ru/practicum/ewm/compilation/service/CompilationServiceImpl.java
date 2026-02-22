package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationMapper mapper;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto adminCreateCompilation(NewCompilationDto dto) {
        Compilation comp = mapper.mapNewCompilationToEntity(dto);
        comp.setEvents(getEvents(dto.getEvents()));
        return mapper.mapEntityToDto(compilationRepository.save(comp));
    }

    @Override
    public CompilationDto adminUpdateCompilation(long compId, UpdateCompilationDto dto) {
        Compilation oldComp = getCompilation(compId);
        Compilation updated = updateCompilation(oldComp, dto);
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            updated.setEvents(eventRepository.findAllById(dto.getEvents()));
        }
        return mapper.mapEntityToDto(compilationRepository.save(updated));
    }

    @Override
    public void adminRemoveCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getPublicCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Compilation> page = getPage(pinned, pageable);
        return page.stream().map(mapper::mapEntityToDto).toList();
    }

    @Override
    public CompilationDto getPublicCompilationById(long compId) {
        return mapper.mapEntityToDto(getCompilation(compId));
    }

    private List<Event> getEvents(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return eventRepository.findAllById(ids);
    }

    private Page<Compilation> getPage(Boolean pinned, Pageable pageable) {
        if (pinned == null) {
            return compilationRepository.findAll(pageable);
        } else {
            return compilationRepository.findAllByPinned(pinned, pageable);
        }
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> new CompilationNotFound(compId));
    }

    private Compilation updateCompilation(Compilation old, UpdateCompilationDto dto) {
        if (dto.getTitle() != null) {
            old.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            old.setPinned(dto.getPinned());
        }
        return old;
    }
}
