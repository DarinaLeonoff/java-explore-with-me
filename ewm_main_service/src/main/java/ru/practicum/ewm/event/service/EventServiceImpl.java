package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.EventSpecification;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.IllegalStateException;
import ru.practicum.ewm.exception.notFound.CategoryNotFound;
import ru.practicum.ewm.exception.notFound.EventNotFound;
import ru.practicum.ewm.exception.notFound.UserNotFound;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private final EventMapper mapper;

    private final StatsClient client;

    @Override
    public List<EventFullDto> adminGetEvent(List<Long> users, List<String> states, List<Integer> categories,
            String rangeStart, String rangeEnd, int from, int size) {

        LocalDateTime start = parseDate(rangeStart);
        LocalDateTime end = parseDate(rangeEnd);

        Specification<Event> spec = EventSpecification.withAdminFilters(users, states, categories, start, end);
        Pageable pageable = buildPageable(from / size, size, "id", Sort.Direction.ASC);
        Page<Event> events = repository.findAll(spec, pageable);

        return events.stream().map(mapper::mapEventToFullDto).toList();
    }

    @Override
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest request) {

        Event event = getEvent(eventId);
        Category category = getCategory(request.getCategory());

        Event updated = updateEvent(event, request, category);
        updateState(updated, request);
        repository.save(updated);

        return mapper.mapEventToFullDto(updated);
    }

    @Override
    public EventFullDto userPostNewEvent(NewEventDto dto, long userId) {
        Category cat = getCategory(dto.getCategory());
        User user = getUser(userId);

        Event event = mapper.mapNewEventToEvent(dto);
        event.setCategory(cat);
        event.setInitiator(user);

        return mapper.mapEventToFullDto(repository.save(event));
    }

    @Override
    public List<EventShortDto> userGetUserEvents(long userId, int from, int size) {
        Pageable pageable = buildPageable(from / size, size, "id", Sort.Direction.ASC);
        Page<Event> events = repository.findAllByInitiatorId(userId, pageable);
        return events.stream().map(mapper::mapEventToShortDto).toList();
    }

    @Override
    public EventFullDto userGetUserEventById(long userId, long eventId) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId);
        return mapper.mapEventToFullDto(event);
    }

    @Override
    public EventFullDto userUpdateUserEvent(long userId, long eventId, UpdateEventUserRequest request) {
        User user = getUser(userId);
        Event event = getUserEvent(eventId, userId);

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Published events can not be updated by users");
        }

        Category category = getCategory(request.getCategory());
        Event updated = updateEvent(event, request, category);

        if (request.getStateAction() == StateUserAction.CANCEL_REVIEW) {
            updated.setState(EventState.CANCELED);
        } else {
            updated.setState(EventState.PENDING);
        }

        repository.save(updated);

        return mapper.mapEventToFullDto(updated);
    }

    @Override
    public List<EventShortDto> getPublicEventList(String text, List<Integer> categories, Boolean paid,
            String rangeStart, String rangeEnd, Boolean onlyAvailable, SortType sort, int from, int size, String ip) {
        //получение корректной даты
        LocalDateTime start = parseDate(rangeStart);
        LocalDateTime end = parseDate(rangeEnd);

        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalStateException("rangeStart must be before rangeEnd");
        }

        // фиксация просмотра страницы
        client.hit(Constants.APP, "/events", ip);

        //получения списка событий по старым views
        Specification<Event> spec = EventSpecification.withPublicFilters(text, categories, paid, start, end,
                onlyAvailable);
        Pageable pageable = buildPageable(from / size, size, sort == SortType.VIEWS ? "views" : "eventDate",
                Sort.Direction.DESC);
        Page<Event> res = repository.findAll(spec, pageable);

        updateViews(res);

        return res.stream().map(mapper::mapEventToShortDto).toList();
    }

    @Override
    public EventFullDto getPublicEvent(long id, String ip) {
        Event event = repository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(() -> new EventNotFound(id));

        client.hit(Constants.APP, Constants.getEventUri(id), ip);

        try { //время чтобы сервис статистики успел обработать запрос добавления статистики
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }

        List<StatsResponseDto> stats = client.getStats(event.getCreatedOn(), LocalDateTime.now(),
                List.of(Constants.getEventUri(id)), true);

        if (!stats.isEmpty()) {
            event.setViews(stats.getFirst().getHits());
            //кеширование просмотров
            repository.save(event);
        }

        return mapper.mapEventToFullDto(event);
    }

    private LocalDateTime parseDate(String date) {
        return date == null ? null : LocalDateTime.parse(date, Constants.DATE_FORMATTER);
    }

    private Pageable buildPageable(int from, int size, String sortField, Sort.Direction direction) {
        return PageRequest.of(from / size, size, Sort.by(direction, sortField));
    }

    private Category getCategory(Integer id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFound(id));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));
    }

    private Event getEvent(Long id) {
        return repository.findById(id).orElseThrow(() -> new EventNotFound(id));
    }

    private Event getUserEvent(Long eventId, Long userId) {
        return repository.findByIdAndInitiatorId(eventId, userId);
    }

    private Event updateEvent(Event oldEvent, UpdateEventRequest updates, Category category) {

        if (updates == null) {
            return oldEvent;
        }

        // annotation
        if (updates.getAnnotation() != null) {
            oldEvent.setAnnotation(updates.getAnnotation());
        }

        // description
        if (updates.getDescription() != null) {
            oldEvent.setDescription(updates.getDescription());
        }

        // title
        if (updates.getTitle() != null) {
            oldEvent.setTitle(updates.getTitle());
        }

        // eventDate
        if (updates.getEventDate() != null) {
            oldEvent.setEventDate(updates.getEventDate());
        }

        // category
        if (category != null) {
            oldEvent.setCategory(category);
        }

        // location
        if (updates.getLocation() != null) {
            oldEvent.setLocation(updates.getLocation());
        }

        // paid
        if (updates.getPaid() != null) {
            oldEvent.setPaid(updates.getPaid());
        }

        // participant limit
        if (updates.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updates.getParticipantLimit());
        }

        // request moderation
        if (updates.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updates.getRequestModeration());
        }

        return oldEvent;
    }

    private void updateState(Event updated, UpdateEventAdminRequest request) {
        if (updated.getState() == EventState.PENDING) {
            if (request.getStateAction() == StateAdminAction.PUBLISH_EVENT) {
                updated.setState(EventState.PUBLISHED);
                updated.setPublishedOn(LocalDateTime.now());
            } else if (request.getStateAction() == StateAdminAction.REJECT_EVENT) {
                updated.setState(EventState.CANCELED);
            }
        } else {
            throw new ConflictException("Expected state pending, but was " + updated.getState());
        }
    }

    private void updateViews(Page<Event> res) {
        if (!res.isEmpty()) {
            List<String> uris = res.stream().map(e -> Constants.getEventUri(e.getId())).toList();
            LocalDateTime firstCreateOn = res.stream().map(Event::getCreatedOn).min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());
            List<StatsResponseDto> stats = client.getStats(firstCreateOn, LocalDateTime.now(), uris, true);
            Map<String, Long> viewsMap = stats.stream()
                    .collect(Collectors.toMap(StatsResponseDto::getUri, StatsResponseDto::getHits));

            res.forEach(event -> {
                String uri = Constants.getEventUri(event.getId());
                event.setViews(viewsMap.getOrDefault(uri, 0L));
            });
        }
    }
}
