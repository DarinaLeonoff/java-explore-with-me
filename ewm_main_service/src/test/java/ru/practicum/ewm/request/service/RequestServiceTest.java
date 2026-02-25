package ru.practicum.ewm.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.AccessException;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.AccessRightsException;
import ru.practicum.ewm.exception.ConflictException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestMapper mapper;

    @InjectMocks
    private RequestServiceImpl service;

    @Test
    void shouldMakeRequest() {
        User initiator = generateUser(1L);
        Event event = generateEvent(1L);
        event.setInitiator(initiator);
        event.setState(EventState.PUBLISHED);

        User requester = generateUser(2L);
        Request request = generateRequest(1L);
        request.setUser(requester);
        request.setEvent(event);

        ParticipationRequestDto dto = generateParticipationRequestDto(request.getId());
        dto.setRequester(requester.getId());
        dto.setEvent(event.getId());

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.save(any())).thenReturn(request);
        when(mapper.mapRequestToDto(any(Request.class))).thenReturn(dto);

        ParticipationRequestDto result = service.makeRequest(requester.getId(), event.getId());

        assertEquals(dto, result);

        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void shouldFailIfInitiatorMakeRequest() {
        User initiator = generateUser(1L);
        Event event = generateEvent(1L);
        event.setInitiator(initiator);
        event.setState(EventState.PUBLISHED);

        Request request = generateRequest(1L);
        request.setUser(initiator);
        request.setEvent(event);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(initiator.getId())).thenReturn(Optional.of(initiator));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.makeRequest(initiator.getId(), event.getId()));
        assertEquals("Initiator can not make request", ex.getMessage());
    }

    @Test
    void shouldFailIfNotPublishedEvent() {
        User initiator = generateUser(1L);
        Event event = generateEvent(1L);
        event.setInitiator(initiator);

        User requester = generateUser(2L);
        Request request = generateRequest(1L);
        request.setUser(requester);
        request.setEvent(event);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.makeRequest(requester.getId(), event.getId()));
        assertEquals("Event is not published", ex.getMessage());
    }

    @Test
    void shouldFailIfDuplicate() {
        User initiator = generateUser(1L);
        Event event = generateEvent(1L);
        event.setInitiator(initiator);
        event.setState(EventState.PUBLISHED);

        User requester = generateUser(2L);
        Request request = generateRequest(1L);
        request.setUser(requester);
        request.setEvent(event);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.existsByUserIdAndEventId(requester.getId(), event.getId())).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.makeRequest(requester.getId(), event.getId()));
        assertEquals("Duplicate request", ex.getMessage());
    }

    @Test
    void shouldFailIfLimitReached() {
        User initiator = generateUser(1L);
        Event event = generateEvent(1L);
        event.setInitiator(initiator);
        event.setState(EventState.PUBLISHED);
        event.setParticipantLimit(1);
        event.setRequestModeration(false);
        event.setConfirmedRequests(1);

        User requester = generateUser(2L);
        Request request = generateRequest(1L);
        request.setUser(requester);
        request.setEvent(event);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.existsByUserIdAndEventId(requester.getId(), event.getId())).thenReturn(false);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.makeRequest(requester.getId(), event.getId()));
        assertEquals("Limit reached", ex.getMessage());
    }

    @Test
    void shouldConfirmIfLimitNotReached() {
        User initiator = generateUser(1L);
        Event event = generateEvent(1L);
        event.setInitiator(initiator);
        event.setState(EventState.PUBLISHED);
        event.setParticipantLimit(1);
        event.setRequestModeration(false);

        User requester = generateUser(2L);
        Request request = generateRequest(1L);
        request.setUser(requester);
        request.setEvent(event);

        ParticipationRequestDto dto = generateParticipationRequestDto(request.getId());
        dto.setRequester(requester.getId());
        dto.setEvent(event.getId());

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.existsByUserIdAndEventId(requester.getId(), event.getId())).thenReturn(false);
        when(requestRepository.save(any())).thenReturn(request);
        when(mapper.mapRequestToDto(any(Request.class))).thenReturn(dto);

        ParticipationRequestDto result = service.makeRequest(requester.getId(), event.getId());
        assertEquals(dto, result);
    }


    @Test
    void shouldThrowWhenEventNotFound() {
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EventNotFound.class, () -> service.makeRequest(1L, 2L));

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(eventRepository.findById(2L)).thenReturn(Optional.of(new Event()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> service.makeRequest(1L, 2L));
    }

    @Test
    void shouldCreatePendingIfModerationEnabled() {
        User initiator = generateUser(1L);
        Event event = generateEvent(1L);
        event.setInitiator(initiator);
        event.setState(EventState.PUBLISHED);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);

        User requester = generateUser(2L);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.existsByUserIdAndEventId(requester.getId(), event.getId())).thenReturn(false);

        when(requestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.mapRequestToDto(any())).thenReturn(new ParticipationRequestDto());

        service.makeRequest(requester.getId(), event.getId());

        verify(requestRepository).save(argThat(r -> r.getStatus() == RequestState.PENDING));
    }

    @Test
    void shouldConfirmIfNoLimit() {
        User initiator = new User();
        initiator.setId(1L);

        Event event = new Event();
        event.setId(1L);
        event.setParticipantLimit(0);
        event.setState(EventState.PUBLISHED);
        event.setRequestModeration(true);
        event.setInitiator(initiator);

        User user = new User();
        user.setId(2L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(requestRepository.existsByUserIdAndEventId(2L, 1L)).thenReturn(false);
        when(requestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.makeRequest(2L, 1L);

        verify(requestRepository).save(argThat(r -> r.getStatus() == RequestState.CONFIRMED));
    }


    @Test
    void shouldReturnUserRequests() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Request request = new Request();
        ParticipationRequestDto dto = new ParticipationRequestDto();

        when(requestRepository.findAllByUserId(userId)).thenReturn(List.of(request));
        when(mapper.mapRequestToDto(request)).thenReturn(dto);

        List<ParticipationRequestDto> result = service.getUserRequests(userId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenUserNotFoundForGetRequests() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> service.getUserRequests(1L));
    }

    @Test
    void shouldCancelRequest() {
        long userId = 1L;
        long requestId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Request request = new Request();
        ParticipationRequestDto dto = new ParticipationRequestDto();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);
        when(mapper.mapRequestToDto(request)).thenReturn(dto);

        ParticipationRequestDto result = service.cancelRequest(userId, requestId);

        assertEquals(RequestState.CANCELED, request.getStatus());
        assertEquals(dto, result);
    }

    @Test
    void shouldThrowWhenRequestNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(requestRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RequestNotFound.class, () -> service.cancelRequest(1L, 2L));
    }

    @Test
    void shouldReturnEventRequests() {
        Request request = new Request();
        ParticipationRequestDto dto = new ParticipationRequestDto();

        when(requestRepository.findAllByEventId(2L)).thenReturn(List.of(request));
        when(mapper.mapRequestToDto(request)).thenReturn(dto);

        List<ParticipationRequestDto> result = service.getEventRequests(1L, 2L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldAcceptRequests() throws AccessException {

        User initiator = new User();
        initiator.setId(1L);

        Event event = new Event();
        event.setInitiator(initiator);

        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));

        Request req = new Request();
        req.setId(10L);
        req.setStatus(RequestState.PENDING);

        when(requestRepository.findAllById(any())).thenReturn(List.of(req));

        ParticipationRequestDto dto = new ParticipationRequestDto();
        when(mapper.mapRequestToDto(req)).thenReturn(dto);

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(10L));
        request.setStatus(RequestState.CONFIRMED);

        Map<String, List<ParticipationRequestDto>> result = service.acceptRequest(1L, 2L, request);

        assertEquals(RequestState.CONFIRMED, req.getStatus());
        assertTrue(result.containsKey("confirmedRequests"));

        verify(requestRepository).saveAll(any());
        verify(eventRepository).save(any());
    }

    @Test
    void shouldAcceptRequestsIfLimitsNotReached() throws AccessException {

        User initiator = new User();
        initiator.setId(1L);

        Event event = new Event();
        event.setInitiator(initiator);
        event.setParticipantLimit(2);

        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));

        Request req = new Request();
        req.setId(10L);
        req.setStatus(RequestState.PENDING);

        when(requestRepository.findAllById(any())).thenReturn(List.of(req));

        ParticipationRequestDto dto = new ParticipationRequestDto();
        when(mapper.mapRequestToDto(req)).thenReturn(dto);

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(10L));
        request.setStatus(RequestState.CONFIRMED);

        Map<String, List<ParticipationRequestDto>> result = service.acceptRequest(1L, 2L, request);

        assertEquals(RequestState.CONFIRMED, req.getStatus());
        assertTrue(result.containsKey("confirmedRequests"));

        verify(requestRepository).saveAll(any());
        verify(eventRepository).save(any());
    }

    @Test
    void shouldRejectRequests() throws AccessException {

        User initiator = new User();
        initiator.setId(1L);

        Event event = new Event();
        event.setInitiator(initiator);

        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));

        Request req = new Request();
        req.setId(5L);
        req.setStatus(RequestState.PENDING);

        when(requestRepository.findAllById(any())).thenReturn(List.of(req));

        when(mapper.mapRequestToDto(req)).thenReturn(new ParticipationRequestDto());

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(5L));
        request.setStatus(RequestState.REJECTED);

        Map<String, List<ParticipationRequestDto>> result = service.acceptRequest(1L, 2L, request);

        assertEquals(RequestState.REJECTED, req.getStatus());
        assertTrue(result.containsKey("rejectedRequests"));
    }

    @Test
    void shouldFailIfRejectConfirmedRequests() throws AccessException {

        User initiator = new User();
        initiator.setId(1L);

        Event event = new Event();
        event.setInitiator(initiator);

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        Request req = new Request();
        req.setId(5L);
        req.setStatus(RequestState.CONFIRMED);

        when(requestRepository.findAllById(any())).thenReturn(List.of(req));

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(5L));
        request.setStatus(RequestState.REJECTED);

        ConflictException ex = assertThrows(ConflictException.class, () -> service.acceptRequest(1L, 2L, request));

        assertEquals("Confirmed requests can not be rejected", ex.getMessage());
    }

    @Test
    void shouldFailIfConfirmRejectedRequests() throws AccessException {

        User initiator = new User();
        initiator.setId(1L);

        Event event = new Event();
        event.setInitiator(initiator);

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        Request req = new Request();
        req.setId(5L);
        req.setStatus(RequestState.REJECTED);

        when(requestRepository.findAllById(any())).thenReturn(List.of(req));

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(5L));
        request.setStatus(RequestState.CONFIRMED);

        ConflictException ex = assertThrows(ConflictException.class, () -> service.acceptRequest(1L, 2L, request));

        assertEquals("Rejected requests can not be confirmed", ex.getMessage());
    }

    @Test
    void shouldFailAcceptIfLimitReached() throws AccessException {

        User initiator = new User();
        initiator.setId(1L);

        Event event = new Event();
        event.setInitiator(initiator);
        event.setParticipantLimit(1);
        event.setConfirmedRequests(1);

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        Request req = new Request();
        req.setId(5L);
        req.setStatus(RequestState.PENDING);

        when(requestRepository.findAllById(any())).thenReturn(List.of(req));

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(5L));
        request.setStatus(RequestState.CONFIRMED);

        ConflictException ex = assertThrows(ConflictException.class, () -> service.acceptRequest(1L, 2L, request));

        assertEquals("Can not accept. Limit of confirmed participation is reached", ex.getMessage());
    }

    @Test
    void shouldThrowAccessException() {
        Event event = new Event();
        User initiator = new User();
        initiator.setId(99L);
        event.setInitiator(initiator);

        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();

        assertThrows(AccessRightsException.class, () -> service.acceptRequest(1L, 2L, request));
    }

    private Event generateEvent(Long id) {
        return Event.builder().id(id).createdOn(LocalDateTime.now()).title("Title").annotation("Annotation")
                .description("Description").eventDate(LocalDateTime.now().plusDays(5)).paid(true).participantLimit(0)
                .requestModeration(false).confirmedRequests(0).state(EventState.PENDING)
                .location(Location.builder().build()).views(0L).build();
    }

    private User generateUser(Long id) {
        return User.builder().id(id).name("user name" + id).email("userName@ya.ru" + id).build();
    }

    private Category generateCategory(int id) {
        return Category.builder().id(id).name("Music" + id).build();
    }

    private Request generateRequest(Long id) {
        return Request.builder().id(id).status(RequestState.PENDING).created(LocalDateTime.now()).build();
    }

    private ParticipationRequestDto generateParticipationRequestDto(Long id) {
        return ParticipationRequestDto.builder().id(id).status(RequestState.PENDING).created(LocalDateTime.now())
                .build();
    }
}
