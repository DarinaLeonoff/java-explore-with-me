package ru.practicum.ewm.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.notFound.EventNotFound;
import ru.practicum.ewm.exception.notFound.RequestNotFound;
import ru.practicum.ewm.exception.notFound.UserNotFound;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestPrivateServiceImpl implements RequestPrivateService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private RequestMapper mapper;

    @Override
    public ParticipationRequestDto makeRequest(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFound(eventId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));

        LocalDateTime created = LocalDateTime.now();

        Request newRequest = Request.builder().user(user).event(event)
                .status(RequestState.PENDING).created(created).build();
        return mapper.mapRequestToDto(requestRepository.save(newRequest));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));
        return requestRepository.findAllByUserId(userId).stream().map(mapper::mapRequestToDto).toList();
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RequestNotFound(requestId));
        request.setStatus(RequestState.CANCELED);
        return mapper.mapRequestToDto(requestRepository.save(request));
    }
}
