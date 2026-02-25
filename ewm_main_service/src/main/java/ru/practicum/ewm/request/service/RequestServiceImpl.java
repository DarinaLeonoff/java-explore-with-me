package ru.practicum.ewm.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RequestServiceImpl implements RequestService {
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

        Event event = getEvent(eventId);
        User user = getUser(userId);

        userIsNotInitiator(event, userId);
        haveToBePublished(event);
        noDuplicate(userId, eventId);

        Request request = Request.builder().user(user).event(event).status(autoState(event))
                .created(LocalDateTime.now()).build();

        requestRepository.save(request);
        setConfirmedRequest(request);

        return mapper.mapRequestToDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        User user = getUser(userId);
        return requestRepository.findAllByUserId(userId).stream().map(mapper::mapRequestToDto).toList();
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        User user = getUser(userId);
        Request request = getRequest(requestId);
        request.setStatus(RequestState.CANCELED);
        return mapper.mapRequestToDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        return requestRepository.findAllByEventId(eventId).stream().map(mapper::mapRequestToDto).toList();
    }

    @Override
    public Map<String, List<ParticipationRequestDto>> acceptRequest(long userId, long eventId,
            EventRequestStatusUpdateRequest request) throws AccessException {

        Event event = getEvent(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new AccessRightsException("User " + userId + " is not initiator of event.");
        }

        return mapRequestsAfterInitiatorModeration(request, event);
    }

    private void userIsNotInitiator(Event event, long userId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator can not make request");
        }
    }

    private void haveToBePublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published");
        }
    }

    private void noDuplicate(long userId, long eventId) {
        if (requestRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new ConflictException("Duplicate request");
        }
    }

    private RequestState autoState(Event event) {
        long confirmedCount = event.getConfirmedRequests();
        // если нет лимита
        if (event.getParticipantLimit() == 0) {
            return RequestState.CONFIRMED;

            // если модерация выключена
        } else if (!event.isRequestModeration()) {

            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Limit reached");
            }

            return RequestState.CONFIRMED;

            // иначе PENDING
        } else {
            return RequestState.PENDING;
        }
    }

    private void setConfirmedRequest(Request request) {
        if (request.getStatus() == RequestState.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests((int) event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
    }

    private Event getEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFound(eventId));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));
    }

    private Request getRequest(long id) {
        return requestRepository.findById(id).orElseThrow(() -> new RequestNotFound(id));
    }

    private Map<String, List<ParticipationRequestDto>> mapRequestsAfterInitiatorModeration(
            EventRequestStatusUpdateRequest request, Event event) {

        List<Request> requests = requestRepository.findAllById(request.getRequestIds());

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        for (Request r : requests) {

            if (request.getStatus() == RequestState.CONFIRMED) {

                if (r.getStatus() == RequestState.REJECTED) {
                    throw new ConflictException("Rejected requests can not be confirmed");
                }

                if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    throw new ConflictException("Can not accept. Limit of confirmed participation is reached");
                }

                r.setStatus(RequestState.CONFIRMED);
                confirmed.add(r);

                event.setConfirmedRequests(event.getConfirmedRequests() + 1);

            } else {
                if (r.getStatus() == RequestState.CONFIRMED) {
                    throw new ConflictException("Confirmed requests can not be rejected");
                }
                r.setStatus(RequestState.REJECTED);
                rejected.add(r);
            }
        }
        requestRepository.saveAll(requests);
        eventRepository.save(event);

        return Map.of("confirmedRequests", confirmed.stream().map(mapper::mapRequestToDto).toList(), "rejectedRequests",
                rejected.stream().map(mapper::mapRequestToDto).toList());
    }
}
