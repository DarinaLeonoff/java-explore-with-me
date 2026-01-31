package ru.practicum.stats.server.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.stats.server.model.StatEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatMapperTest {
    private StatMapper mapper = new StatMapperImpl();

    private final LocalDateTime now = LocalDateTime.now();
    private final StatEntity entity = StatEntity.builder().app("ewm-main-server").uri("/stats").ip("192.163.0.1").created(now).build();
    private final StatsRequestDto dtoReq = StatsRequestDto.builder().app("ewm-main-server").uri("/stats").ip("192.163.0.1").created(now).build();

    @Test
    void testConvertingRequestDtoToEntity() {
        StatEntity result = mapper.mapStatsRequestDtoToStatEntity(dtoReq);

        assertEquals(dtoReq.getIp(), result.getIp());
        assertEquals(dtoReq.getApp(), result.getApp());
        assertEquals(dtoReq.getUri(), result.getUri());
        assertEquals(dtoReq.getCreated(), result.getCreated());
    }

    @Test
    void testConvertingEntityToResponseDto() {
        StatsResponseDto result = mapper.mapEntityToResponseDto(entity, 5);

        assertEquals(entity.getApp(), result.getApp());
        assertEquals(entity.getUri(), result.getUri());
        assertEquals(5, result.getHits());
    }
}

