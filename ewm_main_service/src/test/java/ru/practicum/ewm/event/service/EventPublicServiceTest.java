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
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.notFound.EventNotFound;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventPublicServiceTest {
    @Mock
    private EventRepository repository;

    @Mock
    private EventMapper mapper;

    @InjectMocks
    private EventPublicServiceImpl service;


    @Test
    void shouldReturnEventShortDtoList() {
        // given
        Event event = Event.builder()
                .id(1L)
                .build();

        EventShortDto dto = EventShortDto.builder()
                .id(1L)
                .build();

        when(repository.getEventsByFilters(
                any(), any(), any(), any(), any(), anyBoolean(), any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(event)));

        when(mapper.mapEventToShortDto(event)).thenReturn(dto);

        // when
        List<EventShortDto> result = service.getEventList(
                "text",
                List.of(1, 2),
                true,
                "2026-01-01T00:00:00",
                "2026-12-31T00:00:00",
                true,
                SortType.EVENT_DATE,
                0,
                10
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);

        verify(repository).getEventsByFilters(
                eq("text"),
                eq(List.of(1, 2)),
                eq(true),
                eq(LocalDateTime.parse("2026-01-01T00:00:00")),
                eq(LocalDateTime.parse("2026-12-31T00:00:00")),
                eq(true),
                any(Pageable.class)
        );
    }

    //sorted by view
    @Test
    void shouldSortByViewsWhenSortTypeViews() {
        when(repository.getEventsByFilters(
                any(), any(), any(), any(), any(), anyBoolean(), any(Pageable.class)
        )).thenReturn(Page.empty());

        service.getEventList(
                null, null, null,
                null, null,
                false,
                SortType.VIEWS,
                0, 10
        );

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).getEventsByFilters(
                any(), any(), any(), any(), any(), anyBoolean(), captor.capture()
        );

        Pageable pageable = captor.getValue();
        Sort.Order order = pageable.getSort().iterator().next();

        assertThat(order.getProperty()).isEqualTo("views");
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    //sorted by eventdate
    @Test
    void shouldSortByDateWhenSortTypeEventDate() {
        when(repository.getEventsByFilters(
                any(), any(), any(), any(), any(), anyBoolean(), any(Pageable.class)
        )).thenReturn(Page.empty());

        service.getEventList(
                null, null, null,
                null, null,
                false,
                SortType.EVENT_DATE,
                0, 10
        );

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).getEventsByFilters(
                any(), any(), any(), any(), any(), anyBoolean(), captor.capture()
        );

        Pageable pageable = captor.getValue();
        Sort.Order order = pageable.getSort().iterator().next();

        assertThat(order.getProperty()).isEqualTo("eventDate");
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }


    //filter available only = false
    @Test
    void shouldPassOnlyAvailableFalse() {
        when(repository.getEventsByFilters(
                any(), any(), any(), any(), any(), anyBoolean(), any(Pageable.class)
        )).thenReturn(Page.empty());

        service.getEventList(
                null, null, null,
                null, null,
                false,
                null,
                0, 10
        );

        verify(repository).getEventsByFilters(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(false),
                any(Pageable.class)
        );
    }

    @Test
    void shouldReturnEventFullDto() {
        Event event = Event.builder().id(1L).build();
        EventFullDto dto = EventFullDto.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(event));
        when(mapper.mapEventToFullDto(event)).thenReturn(dto);

        EventFullDto result = service.getEvent(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEvent(1L))
                .isInstanceOf(EventNotFound.class);
    }
}

