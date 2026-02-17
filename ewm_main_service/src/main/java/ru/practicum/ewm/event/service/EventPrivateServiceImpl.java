package ru.practicum.ewm.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.service.CategoryPublicService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.StateUserAction;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventUserRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.notFound.CategoryNotFound;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserPublicService;

import java.util.List;

@Service
public class EventPrivateServiceImpl implements EventPrivateService {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventMapper mapper;

    @Autowired
    private CategoryPublicService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserPublicService userService;

    @Override
    public EventFullDto postNewEvent(NewEventDto dto, long userId) {
        CategoryDto cat = categoryService.getCategory(dto.getCategory());
        UserDto user = userService.getUserById(userId);
        Event event = repository.save(mapper.mapNewEventToEvent(dto, cat, user));
        repository.flush();
        return mapper.mapEventToFullDto(event);
    }

    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Page<Event> events = repository.findAllByInitiatorId(userId, pageable);
        return events.stream().map(mapper::mapEventToShortDto).toList();
    }

    @Override
    public EventFullDto getUserEventById(long userId, long eventId) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId);
        return mapper.mapEventToFullDto(event);
    }

    @Override
    public EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest request) {
        UserDto user = userService.getUserById(userId);
        Event event = repository.findByIdAndInitiatorId(eventId, userId);
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Published events can not be updated by users");
        }
        Category category;
        if (request.getCategory() != null) {
            category = categoryRepository.findById(request.getCategory()).orElseThrow(() -> new CategoryNotFound(request.getCategory()));
        } else {
            category = null;
        }
        Event updated = mapper.updateEvent(event, request, category);
        if (request.getStateAction() == StateUserAction.CANCEL_REVIEW) {
            updated.setState(EventState.CANCELED);
        } else {
            updated.setState(EventState.PENDING);
        }
        repository.save(updated);
        repository.flush();
        return mapper.mapEventToFullDto(updated);
    }
}
