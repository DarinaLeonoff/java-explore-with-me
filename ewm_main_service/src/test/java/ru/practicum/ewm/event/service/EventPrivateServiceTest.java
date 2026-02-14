package ru.practicum.ewm.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryPublicService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.StateUserAction;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserPublicService;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventUserRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventPrivateServiceTest {

    @Mock
    private EventRepository repository;

    @Mock
    private EventMapper mapper;

    @Mock
    private CategoryPublicService categoryService;

    @Mock
    private UserPublicService userService;

    @InjectMocks
    private EventPrivateServiceImpl service;

    @Test
    void shouldCreateNewEvent() {
        long userId = 1L;

        NewEventDto dto = new NewEventDto();
        dto.setCategory(5);

        CategoryDto cat = new CategoryDto();
        UserDto user = new UserDto();

        Event event = new Event();
        Event saved = new Event();
        EventFullDto resultDto = new EventFullDto();

        when(categoryService.getCategory(5)).thenReturn(cat);
        when(userService.getUserById(userId)).thenReturn(user);
        when(mapper.mapNewEventToEvent(dto, cat, user)).thenReturn(event);
        when(repository.save(event)).thenReturn(saved);
        when(mapper.mapEventToFullDto(saved)).thenReturn(resultDto);

        EventFullDto result = service.postNewEvent(dto, userId);

        assertEquals(resultDto, result);

        verify(categoryService).getCategory(5);
        verify(userService).getUserById(userId);
        verify(repository).save(event);
    }

    @Test
    void shouldReturnUserEvents() {
        long userId = 1L;

        Event event = new Event();
        EventShortDto dto = new EventShortDto();

        Page<Event> page = new PageImpl<>(List.of(event));

        when(repository.findAllByInitiatorId(eq(userId), any()))
                .thenReturn(page);

        when(mapper.mapEventToShortDto(event)).thenReturn(dto);

        List<EventShortDto> result = service.getUserEvents(userId, 0, 10);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(repository).findAllByInitiatorId(eq(userId), any());
    }

    @Test
    void shouldReturnUserEventById() {
        Event event = new Event();
        EventFullDto dto = new EventFullDto();

        when(repository.findByIdAndInitiatorId(2L, 1L)).thenReturn(event);
        when(mapper.mapEventToFullDto(event)).thenReturn(dto);

        EventFullDto result = service.getUserEventById(1L, 2L);

        assertEquals(dto, result);

        verify(repository).findByIdAndInitiatorId(2L, 1L);
    }

    @Test
    void shouldUpdateEventWithCategory() {
        long userId = 1L;
        long eventId = 2L;

        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setCategory(5);

        UserDto user = new UserDto();
        CategoryDto category = new CategoryDto();

        Event event = new Event();
        Event updated = new Event();
        Event saved = new Event();
        EventFullDto dto = new EventFullDto();

        when(userService.getUserById(userId)).thenReturn(user);
        when(repository.findByIdAndInitiatorId(eventId, userId)).thenReturn(event);
        when(categoryService.getCategory(5)).thenReturn(category);
        when(mapper.updateEvent(event, request, category)).thenReturn(updated);
        when(repository.save(updated)).thenReturn(saved);
        when(mapper.mapEventToFullDto(saved)).thenReturn(dto);

        EventFullDto result = service.updateUserEvent(userId, eventId, request);

        assertEquals(dto, result);

        verify(categoryService).getCategory(5);
    }

    @Test
    void shouldCancelReview() {
        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setStateAction(StateUserAction.CANCEL_REVIEW);

        Event updated = new Event();

        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(repository.findByIdAndInitiatorId(2L, 1L)).thenReturn(new Event());
        when(mapper.updateEvent(any(), eq(request), any()))
                .thenReturn(updated);
        when(repository.save(updated)).thenReturn(updated);
        when(mapper.mapEventToFullDto(updated)).thenReturn(new EventFullDto());

        service.updateUserEvent(1L, 2L, request);

        assertEquals(EventState.CANCELED, updated.getState());
    }


}
