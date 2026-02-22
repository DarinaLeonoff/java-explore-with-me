package ru.practicum.ewm.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.StateUserAction;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventUserRequest;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

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
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventServiceImpl service;

    @Test
    void shouldCreateNewEvent() {
        long userId = 1L;

        NewEventDto dto = new NewEventDto();
        dto.setCategory(5);

        Category cat = new Category();
        User user = new User();

        Event event = new Event();
        Event saved = new Event();
        EventFullDto resultDto = new EventFullDto();

        when(categoryRepository.findById(5)).thenReturn(Optional.of(cat));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.mapNewEventToEvent(dto)).thenReturn(event);
        when(repository.save(event)).thenReturn(saved);
        when(mapper.mapEventToFullDto(saved)).thenReturn(resultDto);

        EventFullDto result = service.userPostNewEvent(dto, userId);

        assertEquals(resultDto, result);

        verify(categoryRepository).findById(5);
        verify(userRepository).findById(userId);
        verify(repository).save(event);
    }

    @Test
    void shouldReturnUserEvents() {
        long userId = 1L;

        Event event = new Event();
        EventShortDto dto = new EventShortDto();

        Page<Event> page = new PageImpl<>(List.of(event));

        when(repository.findAllByInitiatorId(eq(userId), any())).thenReturn(page);

        when(mapper.mapEventToShortDto(event)).thenReturn(dto);

        List<EventShortDto> result = service.userGetUserEvents(userId, 0, 10);

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

        EventFullDto result = service.userGetUserEventById(1L, 2L);

        assertEquals(dto, result);

        verify(repository).findByIdAndInitiatorId(2L, 1L);
    }

    @Test
    void shouldUpdateEventWithCategory() {
        long userId = 1L;
        long eventId = 2L;

        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setCategory(5);

        User user = new User();
        Category category = new Category();

        Event event = new Event();
        Event updated = new Event();
        Event saved = new Event();
        EventFullDto dto = new EventFullDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.findByIdAndInitiatorId(eventId, userId)).thenReturn(event);
        when(categoryRepository.findById(5)).thenReturn(Optional.of(category));
        when(mapper.updateEvent(event, request, category)).thenReturn(updated);
        when(repository.save(updated)).thenReturn(saved);
        when(mapper.mapEventToFullDto(saved)).thenReturn(dto);

        EventFullDto result = service.userUpdateUserEvent(userId, eventId, request);

        assertEquals(dto, result);

        verify(categoryRepository).findById(5);
    }

    @Test
    void shouldCancelReview() {
        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setStateAction(StateUserAction.CANCEL_REVIEW);

        Event updated = new Event();

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(repository.findByIdAndInitiatorId(2L, 1L)).thenReturn(new Event());
        when(mapper.updateEvent(any(), eq(request), any())).thenReturn(updated);
        when(repository.save(updated)).thenReturn(updated);
        when(mapper.mapEventToFullDto(updated)).thenReturn(new EventFullDto());

        service.userUpdateUserEvent(1L, 2L, request);

        assertEquals(EventState.CANCELED, updated.getState());
    }


}
