package ru.practicum.stats.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsClientTest {

    @Mock
    private RestTemplate restTemplate;

    private StatsClientImpl statsClient;

    private final String statsServiceUrl = "http://localhost::9090";

    @BeforeEach
    void setUp() {
        statsClient = new StatsClientImpl(restTemplate, statsServiceUrl);
    }

    @Test
    void hit_shouldSendPostRequestWithCorrectBodyAndHeaders() {

        // given
        String app = "ewm-main-server";
        String uri = "/hit";
        String ip = "192.163.0.1";

        StatsRequestDto dto = StatsRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .created(LocalDateTime.now())
                .build();

        ArgumentCaptor<HttpEntity<StatsRequestDto>> captor =
                ArgumentCaptor.forClass(HttpEntity.class);

        // when
        statsClient.hit(app, ip);

        // then
        verify(restTemplate).postForEntity(
                eq(statsServiceUrl+uri),
                captor.capture(),
                eq(Void.class)
        );

        HttpEntity<StatsRequestDto> entity = captor.getValue();
        StatsRequestDto body = entity.getBody();
        HttpHeaders headers = entity.getHeaders();

        assertThat(body).isNotNull();
        assertThat(body.getApp()).isEqualTo(app);
        assertThat(body.getUri()).isEqualTo(uri);
        assertThat(body.getIp()).isEqualTo(ip);
        assertThat(body.getCreated()).isNotNull();

        assertThat(headers.getContentType())
                .isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void getStats_shouldCallGetForObjectAndReturnResponse() {
        // given
        Map<String, String> params = Map.of(
                "app", "ewm-main-server",
                "uri", "/stats"
        );

        StatsResponseDto responseDto = StatsResponseDto.builder()
                .app("ewm-main-server")
                .uri("/stats")
                .hits(1)
                .build();

        when(restTemplate.getForObject(
                eq(statsServiceUrl+"/stats"),
                eq(StatsResponseDto.class),
                eq(params)
        )).thenReturn(responseDto);

        // when
        StatsResponseDto result = statsClient.getStats(params);

        // then
        assertThat(result).isEqualTo(responseDto);

        verify(restTemplate).getForObject(
                statsServiceUrl+"/stats",
                StatsResponseDto.class,
                params
        );
    }
}
