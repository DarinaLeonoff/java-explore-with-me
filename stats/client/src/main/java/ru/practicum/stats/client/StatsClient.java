package ru.practicum.stats.client;

import ru.practicum.dto.StatsResponseDto;

import java.util.Map;

public interface StatsClient {
    void hit(String app, String uri, String ip);

    StatsResponseDto getStats(Map<String, String> params);
}
