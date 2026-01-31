package ru.practicum.stats.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.stats.server.mapper.StatMapper;
import ru.practicum.stats.server.model.StatEntity;
import ru.practicum.stats.server.repository.StatRepository;

import java.util.List;

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

    public StatsResponseDto getStats(){
        List<StatEntity> entitys = repository.findAll();
        return mapper.mapEntityToResponseDto(entitys.getFirst(), entitys.size());
    }

}
