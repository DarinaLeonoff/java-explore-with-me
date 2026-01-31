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

    private StatsRequestDto dto = new StatsRequestDto();
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.of(2022, 9, 1, 0, 0);
        end = LocalDateTime.of(2022, 9, 30, 23, 59);
    }

    @Test
    void hitTest() {
        when(mapper.mapStatsRequestDtoToStatEntity(dto)).thenReturn(new StatEntity());

        service.hit(dto);

        verify(repository).save(any(StatEntity.class));
    }

    //    uris != null && !empty && unique == true
    @Test
    void getStats_withUris_andUniqueTrue() {
        List<String> uris = List.of("/hit");
        Boolean unique = true;

        when(repository.findAllByUrisBetweenAndIpIsUnique(uris, start, end)).thenReturn(List.of(entity("app", "/hit"), entity("app", "/hit")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findAllByUrisBetweenAndIpIsUnique(uris, start, end);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHits()).isEqualTo(2);
    }

    //    uris != null && !empty && unique == false/null
    @Test
    void getStats_withUris_andUniqueFalse() {
        List<String> uris = List.of("/hit");
        Boolean unique = false;

        when(repository.findAllByUriInAndCreatedBetween(uris, start, end)).thenReturn(List.of(entity("app", "/hit"), entity("app", "/hit"), entity("app", "/hit")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findAllByUriInAndCreatedBetween(uris, start, end);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHits()).isEqualTo(3);
    }

    //    uris == null/empty && unique == true
    @Test
    void getStats_withoutUris_andUniqueTrue() {
        List<String> uris = null;
        Boolean unique = true;

        when(repository.findAllByCreatedBetweenAndIpIsUnique(start, end)).thenReturn(List.of(entity("app1", "/hit"), entity("app2", "/hit")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findAllByCreatedBetweenAndIpIsUnique(start, end);
        verifyNoMoreInteractions(repository);

        assertThat(result).hasSize(2);
    }

    //    uris == null/empty && unique == false/null
    @Test
    void getStats_withoutUris_andUniqueFalse() {
        List<String> uris = null;
        Boolean unique = null;

        when(repository.findAllByCreatedBetween(start, end)).thenReturn(List.of(entity("app", "/hit"), entity("app", "/hit"), entity("app", "/view")));

        List<StatsResponseDto> result = service.getStats(start, end, uris, unique);

        verify(repository).findAllByCreatedBetween(start, end);
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
