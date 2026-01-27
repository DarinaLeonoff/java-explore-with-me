package ru.practicum.stats.client;

import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.StatsRequestDto;

import java.time.LocalDateTime;

@AllArgsConstructor
public class StatsClientImpl implements StatsClient {

    private static final Log log = LogFactory.getLog(StatsClientImpl.class);
    private final RestTemplate restTemplate;
    private final String statsServiceUrl;

    @Override
    public void hit(String app, String uri, String ip) {
        StatsRequestDto dto = StatsRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(
                statsServiceUrl + "/hit",
                new HttpEntity<>(dto, headers),
                Void.class
        );
    }
}
