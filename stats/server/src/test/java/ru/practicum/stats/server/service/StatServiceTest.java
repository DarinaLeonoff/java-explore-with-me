package ru.practicum.stats.server.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.stats.server.mapper.StatMapper;
import ru.practicum.stats.server.model.StatEntity;
import ru.practicum.stats.server.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Transactional
public class StatServiceTest {
    @InjectMocks
    private StatService service;
    @Mock
    private StatMapper mapper;

    @Mock
    private StatRepository repository;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.of(2022, 9, 1, 0, 0);
        end = LocalDateTime.of(2022, 9, 30, 23, 59);
    }

    @Test
    void hitTest() {
        StatsRequestDto dto = StatsRequestDto.builder()
                .app("main")
                .uri("/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        StatEntity entity = StatEntity.builder()
                .app("main")
                .uri("/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();

        when(mapper.mapStatsRequestDtoToStatEntity(dto)).thenReturn(entity);
        when(repository.save(any(StatEntity.class)))
                .thenReturn(entity);

        service.hit(dto);

        verify(repository).save(any(StatEntity.class));
    }

    //    uris != null && !empty && unique == true
    @Test
    void getStatsWithUrisAndUniqueTrue() {
        List<String> uris = List.of("/hit");
        Boolean unique = true;

        when(repository.findAllUniqueIpByUrisBetween(uris, start, end)).thenReturn(List.of(entity("app", "/hit"), entity("app", "/hit")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findAllUniqueIpByUrisBetween(uris, start, end);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHits()).isEqualTo(2);
    }

    //    uris != null && !empty && unique == false/null
    @Test
    void getStatsWithUrisAndUniqueFalse() {
        List<String> uris = List.of("/hit");
        Boolean unique = false;

        when(repository.findAllByUriInAndTimestampBetween(uris, start, end)).thenReturn(List.of(entity("app", "/hit"), entity("app", "/hit"), entity("app", "/hit")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findAllByUriInAndTimestampBetween(uris, start, end);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHits()).isEqualTo(3);
    }

    //    uris == null/empty && unique == true
    @Test
    void getStatsWithoutUrisAndUniqueTrue() {
        List<String> uris = null;
        Boolean unique = true;

        when(repository.findAllByTimestampBetweenAndIpIsUnique(start, end)).thenReturn(List.of(entity("app1", "/hit"), entity("app2", "/hit")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findAllByTimestampBetweenAndIpIsUnique(start, end);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(2);
    }

    //    uris == null/empty && unique == false/null
    @Test
    void getStatsWithoutUrisAndUniqueFalse() {
        List<String> uris = null;
        Boolean unique = null;

        when(repository.findAllByTimestampBetween(start, end)).thenReturn(List.of(entity("app", "/hit"), entity("app", "/hit"), entity("app", "/view")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findAllByTimestampBetween(start, end);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(2);

        assertThat(result).anyMatch(r -> r.getUri().equals("/hit") && r.getHits() == 2).anyMatch(r -> r.getUri().equals("/view") && r.getHits() == 1);
    }

    private StatEntity entity(String app, String uri) {
        StatEntity e = new StatEntity();
        e.setApp(app);
        e.setUri(uri);
        return e;
    }

}
