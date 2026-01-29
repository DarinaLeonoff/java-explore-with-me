package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.StatEntity;
import ru.practicum.repository.StatRepository;

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
