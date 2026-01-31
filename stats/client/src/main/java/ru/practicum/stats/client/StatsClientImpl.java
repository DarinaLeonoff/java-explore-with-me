package ru.practicum.stats.client;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
public class StatsClientImpl implements StatsClient {

    private final RestTemplate restTemplate;
    private final String statsServiceUrl;

    @Override
    public void hit(String app, String ip) {
        StatsRequestDto dto = StatsRequestDto.builder()
                .app(app)
                .uri("/hit")
                .ip(ip)
                .created(LocalDateTime.now())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(
                statsServiceUrl + "/hit",
                new HttpEntity<>(dto, headers),
                Void.class
        );
    }

    @Override
    public StatsResponseDto getStats(Map<String, String> params) {
        return restTemplate.getForObject(
                statsServiceUrl + "/stats",
                StatsResponseDto.class,
                params
        );
    }
}
