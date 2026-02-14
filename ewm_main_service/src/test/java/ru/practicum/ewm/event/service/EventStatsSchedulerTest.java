package ru.practicum.ewm.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventStatsSchedulerTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private StatsClient client;

    @InjectMocks
    private EventStatsScheduler scheduler;

    @Test
    void shouldUpdateViews() {
        Event event = new Event();
        event.setId(1L);
        event.setPublishedOn(LocalDateTime.now().minusDays(1));

        Page<Event> page = new PageImpl<>(List.of(event));

        when(eventRepository.findByState(eq(EventState.PUBLISHED), any()))
                .thenReturn(page);

        StatsResponseDto stat = new StatsResponseDto();
        stat.setUri(Constants.getEventUri(1L));
        stat.setHits(50);

        when(client.getStats(any(), any(), any(), eq(true)))
                .thenReturn(List.of(stat));

        scheduler.updateViews();

        assertEquals(50, event.getViews());

        verify(eventRepository).saveAll(page);
    }

    @Test
    void shouldNotCallClientWhenNoEvents() {
        when(eventRepository.findByState(eq(EventState.PUBLISHED), any()))
                .thenReturn(Page.empty());

        scheduler.updateViews();

        verifyNoInteractions(client);
        verify(eventRepository, never()).saveAll(any());
    }

    @Test
    void shouldSetZeroViewsWhenStatsEmpty() {
        Event event = new Event();
        event.setId(1L);
        event.setPublishedOn(LocalDateTime.now().minusDays(1));

        Page<Event> page = new PageImpl<>(List.of(event));

        when(eventRepository.findByState(eq(EventState.PUBLISHED), any()))
                .thenReturn(page);

        when(client.getStats(any(), any(), any(), eq(true)))
                .thenReturn(List.of());

        scheduler.updateViews();

        assertEquals(0, event.getViews());
    }

    @Test
    void shouldUseCorrectDateRange() {
        Event event = new Event();
        event.setId(1L);
        LocalDateTime published = LocalDateTime.now().minusDays(2);
        event.setPublishedOn(published);

        Page<Event> page = new PageImpl<>(List.of(event));

        when(eventRepository.findByState(eq(EventState.PUBLISHED), any()))
                .thenReturn(page);

        ArgumentCaptor<LocalDateTime> startCaptor =
                ArgumentCaptor.forClass(LocalDateTime.class);

        ArgumentCaptor<LocalDateTime> endCaptor =
                ArgumentCaptor.forClass(LocalDateTime.class);

        when(client.getStats(startCaptor.capture(), endCaptor.capture(), anyList(), eq(true)))
                .thenReturn(List.of());

        scheduler.updateViews();

        assertEquals(published, startCaptor.getValue());
        assertNotNull(endCaptor.getValue());
    }

    @Test
    void shouldSendCorrectUris() {
        Event event = new Event();
        event.setId(10L);
        event.setPublishedOn(LocalDateTime.now());

        when(eventRepository.findByState(any(), any()))
                .thenReturn(new PageImpl<>(List.of(event)));

        ArgumentCaptor<List<String>> uriCaptor =
                ArgumentCaptor.forClass(List.class);

        when(client.getStats(any(), any(), uriCaptor.capture(), eq(true)))
                .thenReturn(List.of());

        scheduler.updateViews();

        assertEquals(
                List.of(Constants.getEventUri(10L)),
                uriCaptor.getValue()
        );
    }


}
