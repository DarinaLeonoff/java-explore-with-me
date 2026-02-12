package ru.practicum.stats.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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

        StatsRequestDto dto = StatsRequestDto.builder().app(app).uri(uri).ip(ip).timestamp(LocalDateTime.now()).build();

        ArgumentCaptor<HttpEntity<StatsRequestDto>> captor = ArgumentCaptor.forClass(HttpEntity.class);

        // when
        statsClient.hit(app, uri, ip);

        // then
        verify(restTemplate).postForEntity(eq(statsServiceUrl + uri), captor.capture(), eq(Void.class));

        HttpEntity<StatsRequestDto> entity = captor.getValue();
        StatsRequestDto body = entity.getBody();
        HttpHeaders headers = entity.getHeaders();

        assertThat(body).isNotNull();
        assertThat(body.getApp()).isEqualTo(app);
        assertThat(body.getUri()).isEqualTo(uri);
        assertThat(body.getIp()).isEqualTo(ip);
        assertThat(body.getTimestamp()).isNotNull();

        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void getStats_shouldCallGetForObjectAndReturnResponse() {
        // given
        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/events/2", "/events/1", "/events/3");
        boolean unique = false;

        List<StatsResponseDto> responseList = List.of(StatsResponseDto.builder().app("ewm-main-server").uri("/events/1").hits(10).build());

        ResponseEntity<List<StatsResponseDto>> responseEntity = new ResponseEntity<>(responseList, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        // when
        List<StatsResponseDto> result = statsClient.getStats(start, end, uris, unique);

        // then
        assertThat(result).isEqualTo(responseList);

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }
}
