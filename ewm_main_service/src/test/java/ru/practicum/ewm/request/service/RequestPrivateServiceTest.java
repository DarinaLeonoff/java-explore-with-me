package ru.practicum.ewm.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.AccessRightsException;
import ru.practicum.ewm.exception.notFound.EventNotFound;
import ru.practicum.ewm.exception.notFound.RequestNotFound;
import ru.practicum.ewm.exception.notFound.UserNotFound;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestPrivateServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestMapper mapper;

    @InjectMocks
    private RequestPrivateServiceImpl service;

    @Test
    void shouldMakeRequest() {
        long userId = 1L;
        long eventId = 2L;

        Event event = new Event();
        User user = new User();

        Request saved = new Request();
        ParticipationRequestDto dto = new ParticipationRequestDto();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(saved);
        when(mapper.mapRequestToDto(saved)).thenReturn(dto);

        ParticipationRequestDto result = service.makeRequest(userId, eventId);

        assertEquals(dto, result);

        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void shouldThrowWhenEventNotFound() {
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EventNotFound.class,
                () -> service.makeRequest(1L, 2L));

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(eventRepository.findById(2L)).thenReturn(Optional.of(new Event()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class,
                () -> service.makeRequest(1L, 2L));
    }

    @Test
    void shouldReturnUserRequests() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Request request = new Request();
        ParticipationRequestDto dto = new ParticipationRequestDto();

        when(requestRepository.findAllByUserId(userId))
                .thenReturn(List.of(request));
        when(mapper.mapRequestToDto(request)).thenReturn(dto);

        List<ParticipationRequestDto> result = service.getUserRequests(userId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenUserNotFoundForGetRequests() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class,
                () -> service.getUserRequests(1L));
    }

    @Test
    void shouldCancelRequest() {
        long userId = 1L;
        long requestId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Request request = new Request();
        ParticipationRequestDto dto = new ParticipationRequestDto();

        when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);
        when(mapper.mapRequestToDto(request)).thenReturn(dto);

        ParticipationRequestDto result =
                service.cancelRequest(userId, requestId);

        assertEquals(RequestState.CANCELED, request.getStatus());
        assertEquals(dto, result);
    }

    @Test
    void shouldThrowWhenRequestNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(requestRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RequestNotFound.class,
                () -> service.cancelRequest(1L, 2L));
    }

    @Test
    void shouldReturnEventRequests() {
        Request request = new Request();
        ParticipationRequestDto dto = new ParticipationRequestDto();

        when(requestRepository.findAllByEventId(2L))
                .thenReturn(List.of(request));
        when(mapper.mapRequestToDto(request)).thenReturn(dto);

        List<ParticipationRequestDto> result =
                service.getEventRequests(1L, 2L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldAcceptRequests() throws Exception {
        long userId = 1L;
        long eventId = 2L;

        Event event = new Event();
        User initiator = new User();
        initiator.setId(userId);
        event.setInitiator(initiator);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        Request req = new Request();
        req.setId(10L);

        when(requestRepository.findAllByEventId(eventId))
                .thenReturn(List.of(req));

        ParticipationRequestDto dto = new ParticipationRequestDto();
        when(mapper.mapRequestToDto(req)).thenReturn(dto);

        EventRequestStatusUpdateRequest request =
                new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(10L));
        request.setStatus(RequestState.CONFIRMED);

        Map<String, List<ParticipationRequestDto>> result =
                service.acceptRequest(userId, eventId, request);

        assertEquals(RequestState.CONFIRMED, req.getStatus());
        assertTrue(result.containsKey("confirmedRequests"));
    }

    @Test
    void shouldRejectRequests() throws Exception {
        Event event = new Event();
        User initiator = new User();
        initiator.setId(1L);
        event.setInitiator(initiator);

        when(eventRepository.findById(2L))
                .thenReturn(Optional.of(event));

        Request req = new Request();
        req.setId(5L);

        when(requestRepository.findAllByEventId(2L))
                .thenReturn(List.of(req));

        when(mapper.mapRequestToDto(req))
                .thenReturn(new ParticipationRequestDto());

        EventRequestStatusUpdateRequest request =
                new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(5L));
        request.setStatus(RequestState.REJECTED);

        Map<String, List<ParticipationRequestDto>> result =
                service.acceptRequest(1L, 2L, request);

        assertTrue(result.containsKey("rejectedRequests"));
    }

    @Test
    void shouldThrowAccessException() {
        Event event = new Event();
        User initiator = new User();
        initiator.setId(99L);
        event.setInitiator(initiator);

        when(eventRepository.findById(2L))
                .thenReturn(Optional.of(event));

        EventRequestStatusUpdateRequest request =
                new EventRequestStatusUpdateRequest();

        assertThrows(AccessRightsException.class,
                () -> service.acceptRequest(1L, 2L, request));
    }
}
