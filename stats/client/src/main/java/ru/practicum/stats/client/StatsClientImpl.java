package ru.practicum.stats.client;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
public class StatsClientImpl implements StatsClient {
    public final String formate = "yyyy-MM-dd HH:mm:ss";
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formate);
    private static final Logger log =
            LoggerFactory.getLogger(StatsClientImpl.class);

    private final RestTemplate restTemplate;
    private final String statsServiceUrl;

    @Override
    public void hit(String app, String uri, String ip) {
        StatsRequestDto dto = StatsRequestDto.builder().app(app).uri(uri).ip(ip).timestamp(LocalDateTime.now()).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(statsServiceUrl + "/hit", new HttpEntity<>(dto, headers), Void.class);
        log.info("Stat was sent to stat-service");
    }

    @Override
    public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Getting stats");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(statsServiceUrl + "/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        uris.forEach(uri -> builder.queryParam("uris", uri));

        ResponseEntity<List<StatsResponseDto>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
        });
        log.info("Stats was successful extract.");
        return response.getBody();
    }
}
