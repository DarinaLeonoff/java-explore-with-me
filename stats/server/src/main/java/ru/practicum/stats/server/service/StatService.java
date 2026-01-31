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
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatService {
    @Autowired
    private StatRepository repository;
    @Autowired
    private StatMapper mapper;

    public void hit(StatsRequestDto dto) {
        repository.save(mapper.mapStatsRequestDtoToStatEntity(dto));
    }

    public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<StatEntity> entities;
        if ((uris != null && !uris.isEmpty()) && (unique != null && unique)) {
            entities = repository.findAllByUrisBetweenAndIpIsUnique(uris, start, end);
        } else if (uris != null && !uris.isEmpty()) {
            entities = repository.findAllByUriInAndCreatedBetween(uris, start, end);
        } else if (unique != null && unique) {
            entities = repository.findAllByCreatedBetweenAndIpIsUnique(start, end);
        } else {
            entities = repository.findAllByCreatedBetween(start, end);
        }

        return entities.stream().collect(Collectors.groupingBy(StatEntity::getApp, Collectors.groupingBy(StatEntity::getUri, Collectors.counting()))).entrySet().stream().flatMap(appEntry -> appEntry.getValue().entrySet().stream().map(uriEntry -> new StatsResponseDto(appEntry.getKey(), uriEntry.getKey(), uriEntry.getValue().intValue()))).toList();
    }

}
