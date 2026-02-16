package ru.practicum.ewm.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.exception.notFound.CompilationNotFound;

import java.util.List;

@Service
public class CompilationPublicServiceImpl implements CompilationPublicService {
    @Autowired
    private CompilationRepository repository;

    @Autowired
    private CompilationMapper mapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Compilation> page;

        if (pinned == null) {
            page = repository.findAll(pageable);
        } else {
            page = repository.findAllByPinned(pinned, pageable);
        }

        return page.stream().map(mapper::mapEntityToDto).toList();
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        return mapper.mapEntityToDto(repository.findById(compId).orElseThrow(() -> new CompilationNotFound(compId)));
    }
}
