package ru.practicum.ewm.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.stats.client.StatsClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventPublicServiceTest {
    @Mock
    private EventRepository repository;

    @Mock
    private EventMapper mapper;

    @Mock
    private StatsClient client;

    @InjectMocks
    private EventPublicServiceImpl service;

    @Test
    void shouldReturnMappedEvents() {
        Event event = new Event();
        EventShortDto dto = new EventShortDto();

        Page<Event> page = new PageImpl<>(List.of(event));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        when(mapper.mapEventToShortDto(event)).thenReturn(dto);

        List<EventShortDto> result = service.getEventList("text", List.of(1), true, null, null, true, SortType.EVENT_DATE, 0, 10);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(repository).findAll((Specification<Event>) any(), (Pageable) any());
        verify(mapper).mapEventToShortDto(event);
    }

    @Test
    void shouldSortByViews() {
        Page<Event> page = new PageImpl<>(List.of());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(repository.findAll((Specification<Event>) any(), pageableCaptor.capture())).thenReturn(page);

        service.getEventList(null, null, null, null, null, null, SortType.VIEWS, 0, 10);

        Pageable pageable = pageableCaptor.getValue();

        assertTrue(pageable.getSort().getOrderFor("views").isDescending());
    }

    @Test
    void shouldParseDates() {
        Page<Event> page = new PageImpl<>(List.of());
        when(repository.findAll((Specification<Event>) any(), (Pageable) any())).thenReturn(page);

        service.getEventList(null, null, null, "2025-01-01 10:00:00", "2025-01-02 10:00:00", null, SortType.EVENT_DATE, 0, 10);

        verify(repository).findAll((Specification<Event>) any(), (Pageable) any());
    }

//    @Test
//    void shouldReturnEventAndCallStats() {
//        long id = 1L;
//
//        Event event = new Event();
//        EventFullDto dto = new EventFullDto();
//
//        when(repository.findByIdAndState(anyLong(), any())).thenReturn(Optional.of(event));
//        when(mapper.mapEventToFullDto(event)).thenReturn(dto);
//
//        EventFullDto result = service.getEvent(id, "127.0.0.1");
//
//        assertEquals(dto, result);
//
//        verify(client).hit(any(), any(), eq("127.0.0.1"));
//        verify(mapper).mapEventToFullDto(event);
//    }

//    @Test
//    void shouldThrowWhenEventNotFound() {
//        when(repository.findByIdAndState(anyLong(), any())).thenReturn(Optional.empty());
//
//        assertThrows(EventNotFound.class, () -> service.getEvent(1L, "ip"));
//
//        verifyNoInteractions(client);
//    }


//    @Test
//    void shouldSendCorrectUriToStats() {
//        long id = 5L;
//
//        when(repository.findByIdAndState(anyLong(), any())).thenReturn(Optional.of(new Event()));
//
//        service.getEvent(id, "ip");
//
//        verify(client).hit(eq(Constants.APP), eq(Constants.getEventUri(id)), eq("ip"));
//    }

}

