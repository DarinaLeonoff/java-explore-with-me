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
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.updateDto.StateAdminAction;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.notFound.EventNotFound;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventAdminServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository repository;

    @Mock
    private EventMapper mapper;

    @InjectMocks
    private EventServiceImpl service;

    @Test
    void shouldReturnMappedEvents() {
        List<Long> users = new ArrayList<>(List.of(0L, 1L));
        List<String> states = List.of("PUBLISHED");
        List<Integer> categories = new ArrayList<>(List.of(0, 2));

        Event event = new Event();
        EventFullDto dto = new EventFullDto();

        Page<Event> page = new PageImpl<>(List.of(event));

        when(repository.findAll((Specification<Event>) any(), (Pageable) any())).thenReturn(page);
        when(mapper.mapEventToFullDto(event)).thenReturn(dto);

        List<EventFullDto> result = service.adminGetEvent(users, states, categories, null, null, 0, 10);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(repository).findAll((Specification<Event>) any(), (Pageable) any());
    }

    @Test
    void shouldParseDateRange() {
        Event event = new Event();

        when(repository.findAll((Specification<Event>) any(), (Pageable) any())).thenReturn(
                new PageImpl<>(List.of(event)));

        service.adminGetEvent(new ArrayList<>(List.of(1L)), List.of("PUBLISHED"), new ArrayList<>(List.of(2)),
                "2025-01-01 10:00:00", "2025-01-02 10:00:00", 0, 10);

        verify(repository).findAll((Specification<Event>) any(), (Pageable) any());
    }

    @Test
    void shouldUpdateEvent() {
        Long id = 1L;

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setCategory(5);

        Event event = new Event();
        Category category = new Category();
        Event updated = new Event();
        EventFullDto dto = new EventFullDto();

        when(repository.findById(id)).thenReturn(Optional.of(event));
        when(categoryRepository.findById(5)).thenReturn(Optional.of(category));
        when(mapper.updateEvent(event, request, category)).thenReturn(updated);
        when(mapper.mapEventToFullDto(updated)).thenReturn(dto);

        EventFullDto result = service.adminUpdateEvent(id, request);

        assertEquals(dto, result);
    }

    @Test
    void shouldUpdateWithoutCategory() {
        Long id = 1L;

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();

        when(repository.findById(id)).thenReturn(Optional.of(new Event()));
        when(mapper.updateEvent(any(), eq(request), isNull())).thenReturn(new Event());
        when(mapper.mapEventToFullDto(any())).thenReturn(new EventFullDto());

        service.adminUpdateEvent(id, request);

        verify(categoryRepository, never()).findById(anyInt());
    }

    @Test
    void shouldPublishEvent() {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAdminAction.PUBLISH_EVENT);

        Event updated = new Event();

        when(repository.findById(1L)).thenReturn(Optional.of(new Event()));
        when(mapper.updateEvent(any(), eq(request), any())).thenReturn(updated);
        when(mapper.mapEventToFullDto(updated)).thenReturn(new EventFullDto());

        service.adminUpdateEvent(1L, request);

        assertEquals(EventState.PUBLISHED, updated.getState());
    }

    @Test
    void shouldRejectEvent() {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAdminAction.REJECT_EVENT);

        Event updated = new Event();

        when(repository.findById(1L)).thenReturn(Optional.of(new Event()));
        when(mapper.updateEvent(any(), eq(request), any())).thenReturn(updated);
        when(mapper.mapEventToFullDto(updated)).thenReturn(new EventFullDto());

        service.adminUpdateEvent(1L, request);

        assertEquals(EventState.CANCELED, updated.getState());
    }

    @Test
    void shouldThrowWhenEventNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFound.class, () -> service.adminUpdateEvent(1L, new UpdateEventAdminRequest()));
    }

}
