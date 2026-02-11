package ru.practicum.ewm.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryPublicService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
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
    private UserPublicService userService;

    @Override
    public EventFullDto postNewEvent(NewEventDto dto, long userId) {
        CategoryDto cat = categoryService.getCategory(dto.getCategory());
        UserDto user = userService.getUserById(userId);
        Event event = mapper.mapNewEventToEvent(dto, cat, user);
        return mapper.mapEventToFullDto(repository.save(event));
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


}
