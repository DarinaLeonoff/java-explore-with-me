package ru.practicum.stats.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.stats.server.mapper.StatMapper;
import ru.practicum.stats.server.model.StatEntity;
import ru.practicum.stats.server.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StatService {
    @Autowired
    private StatRepository repository;
    @Autowired
    private StatMapper mapper;

    public void hit(StatsRequestDto dto) {
        StatEntity saved = repository.save(mapper.mapStatsRequestDtoToStatEntity(dto));
        log.info("Saved with id={}", saved.getId());
        log.info("Saved with date={}", saved.getTimestamp());
    }

    public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<String> safeUris = (uris == null || uris.isEmpty()) ? null : uris;


        List<StatsResponseDto> result = Boolean.TRUE.equals(unique) ? repository.findStatsUnique(start, end, safeUris) : repository.findStats(start, end, safeUris);

        return result;
    }
}
