package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.stats.server.model.StatEntity;

@Mapper(componentModel = "spring")
public interface StatMapper {

//    @Mapping(target = "app", source = "app")
    StatEntity mapStatsRequestDtoToStatEntity(StatsRequestDto dto);

    @Mapping(target = "hits", source = "hits")
    StatsResponseDto mapEntityToResponseDto(StatEntity entity, int hits);
}
