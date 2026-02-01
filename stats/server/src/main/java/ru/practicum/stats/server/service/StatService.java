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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<StatEntity> entities;
        if ((uris != null && !uris.isEmpty()) && (unique != null && unique)) {
            entities = repository.findAllUniqueIpByUrisBetween(uris, start, end);
            log.info("Get unique visits with uris. Entities size is {}", entities.size());
        } else if (uris != null && !uris.isEmpty()) {
            entities = repository.findAllByUriInAndTimestampBetween(uris, start, end);
            log.info("Get all visits with uris: {}\n. Entities size is {}", uris.toArray(), entities.size());
        } else if (unique != null && unique) {
            entities = repository.findAllByTimestampBetweenAndIpIsUnique(start, end);
            log.info("Get unique visits. Entities size is {}", entities.size());
        } else {
            entities = repository.findAllByTimestampBetween(start, end);
            log.info("Get all visits. Entities size is {}", entities.size());
        }

        Map<String, Map<String, Long>> statsByAppAndUri =
                entities.stream()
                        .collect(Collectors.groupingBy(
                                StatEntity::getApp,
                                Collectors.groupingBy(
                                        StatEntity::getUri,
                                        Collectors.counting()
                                )
                        ));

// 2. Преобразование Map в список DTO
                return statsByAppAndUri.entrySet().stream()
                        .flatMap(appEntry ->
                                appEntry.getValue().entrySet().stream()
                                        .map(uriEntry ->
                                                new StatsResponseDto(
                                                        appEntry.getKey(),
                                                        uriEntry.getKey(),
                                                        uriEntry.getValue().intValue()
                                                )
                                        )
                        )
                        .toList().stream().sorted(Comparator.comparingInt(StatsResponseDto::getHits).reversed()).toList();
    }

}
