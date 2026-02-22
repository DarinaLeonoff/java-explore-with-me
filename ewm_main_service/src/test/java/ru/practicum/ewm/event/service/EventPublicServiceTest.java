package ru.practicum.ewm.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.IllegalStateException;
import ru.practicum.ewm.exception.notFound.EventNotFound;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventPublicServiceTest {
    @Mock
    private EventRepository repository;

    @Mock
    private EventMapper mapper;

    @Mock
    private StatsClient client;

    @InjectMocks
    private EventServiceImpl service;

    @Test
    void shouldReturnEventList() {
        Event event = new Event();
        event.setId(1L);
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setViews(0L);

        EventShortDto dto = new EventShortDto();

        when(repository.findAll((Specification<Event>) any(), any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(event)));

        when(mapper.mapEventToShortDto(event)).thenReturn(dto);

        //мок статистики
        StatsResponseDto stats = new StatsResponseDto();
        stats.setUri(Constants.getEventUri(1L));
        stats.setHits(10L);

        when(client.getStats(any(), any(), anyList(), eq(true))).thenReturn(List.of(stats));

        List<EventShortDto> result = service.getPublicEventList("text", List.of(1), true, null, null, false,
                SortType.EVENT_DATE, 0, 10, "127.0.0.1");

        assertEquals(1, result.size());
        verify(client).hit(Constants.APP, "/events", "127.0.0.1");
    }

    @Test
    void shouldThrowIfRangeInvalid() {

        String start = "2025-01-02 10:00:00";
        String end = "2025-01-01 10:00:00";

        assertThrows(IllegalStateException.class,
                () -> service.getPublicEventList(null, null, null, start, end, false, SortType.EVENT_DATE, 0, 10,
                        "ip"));

        verifyNoInteractions(repository);
    }

    @Test
    void shouldReturnEvent() throws InterruptedException {

        Event event = new Event();
        event.setId(1L);

        EventFullDto dto = new EventFullDto();

        when(repository.findByIdAndState(1L, EventState.PUBLISHED)).thenReturn(Optional.of(event));

        when(client.getStats(any(), any(), any(), anyBoolean())).thenReturn(List.of(new StatsResponseDto()));

        when(mapper.mapEventToFullDto(event)).thenReturn(dto);

        EventFullDto result = service.getPublicEvent(1L, "ip");

        assertEquals(dto, result);
        verify(client).hit(Constants.APP, Constants.getEventUri(1L), "ip");
    }

    @Test
    void shouldThrowIfEventNotFound() {

        when(repository.findByIdAndState(anyLong(), any())).thenReturn(Optional.empty());

        assertThrows(EventNotFound.class, () -> service.getPublicEvent(1L, "ip"));
    }

    @Test
    void shouldUpdateViewsFromStats() throws InterruptedException {

        Event event = new Event();
        event.setId(1L);

        StatsResponseDto stats = new StatsResponseDto();
        stats.setHits(10L);

        when(repository.findByIdAndState(1L, EventState.PUBLISHED)).thenReturn(Optional.of(event));

        when(client.getStats(any(), any(), any(), anyBoolean())).thenReturn(List.of(stats));

        when(mapper.mapEventToFullDto(event)).thenReturn(new EventFullDto());

        service.getPublicEvent(1L, "ip");

        assertEquals(10L, event.getViews());
    }

    @Test
    void shouldRetryFindEvent() throws InterruptedException {

        Event event = new Event();

        when(repository.findByIdAndState(anyLong(), any())).thenReturn(Optional.empty()).thenReturn(Optional.of(event));

        when(client.getStats(any(), any(), any(), anyBoolean())).thenReturn(List.of());

        when(mapper.mapEventToFullDto(event)).thenReturn(new EventFullDto());

        service.getPublicEvent(1L, "ip");

        verify(repository, atLeast(2)).findByIdAndState(1L, EventState.PUBLISHED);
    }

    @Test
    void shouldCallStatsHit() {

        when(repository.findAll((Specification<Event>) any(), (Pageable) any())).thenReturn(Page.empty());

        service.getPublicEventList(null, null, null, null, null, false, SortType.EVENT_DATE, 0, 10, "ip");

        verify(client).hit(Constants.APP, "/events", "ip");
    }
}

