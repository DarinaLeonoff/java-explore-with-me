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

        when(repository.findStatsUnique(start, end, uris))
                .thenReturn(List.of(response("app", "/hit")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findStatsUnique(start, end, uris);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(1);
    }

    //    uris != null && !empty && unique == false/null
    @Test
    void getStatsWithUrisAndUniqueFalse() {
        List<String> uris = List.of("/hit");
        Boolean unique = false;

        when(repository.findStats(start, end, uris))
                .thenReturn(List.of(response("app", "/hit"), response("app1", "/hit"), response("app2", "/hit")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findStats(start, end, uris);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(3);
    }

    //    uris == null/empty && unique == true
    @Test
    void getStatsWithoutUrisAndUniqueTrue() {
        List<String> uris = null;
        Boolean unique = true;

        when(repository.findStatsUnique(start, end, uris)).thenReturn(List.of(response("app1", "/hit"), response("app2", "/hit")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findStatsUnique(start, end, uris);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(2);
    }

    //    uris == null/empty && unique == false/null
    @Test
    void getStatsWithoutUrisAndUniqueFalse() {
        List<String> uris = null;
        Boolean unique = null;

        when(repository.findStats(start, end, uris))
                .thenReturn(List.of(response("app", "/hit"), response("app", "/view")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findStats(start, end, uris);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(2);
    }

    private StatsResponseDto response(String app, String uri) {
        StatsResponseDto e = new StatsResponseDto();
        e.setApp(app);
        e.setUri(uri);
        e.setHits(1);
        return e;
    }

}
