package ru.practicum.ewm.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryPublicService;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.updateDto.StateAdminAction;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.EventSpecification;
import ru.practicum.ewm.exception.notFound.EventNotFound;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventAdminServiceImpl implements EventAdminService {

    @Autowired
    private CategoryPublicService categoryService;

    @Autowired
    private EventRepository repository;

    @Autowired
    private EventMapper mapper;

    @Override
    public List<EventFullDto> getEvent(List<Long> users, List<String> states, List<Integer> categories,
            String rangeStart, String rangeEnd, int from, int size) {
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, Constants.DATE_FORMATTER);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, Constants.DATE_FORMATTER);
        Specification<Event> spec = EventSpecification.withAdminFilters(users, states, categories, start, end);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Page<Event> events = repository.findAll(spec, pageable);
        return events.stream().map(mapper::mapEventToFullDto).toList();
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request) {
        Event event = repository.findById(eventId).orElseThrow(() -> new EventNotFound(eventId));
        CategoryDto category = request.getCategory() == null ? null : categoryService.getCategory(request.getCategory());
        Event updated = mapper.updateEvent(event, request, category);
        if (request.getStateAction() == StateAdminAction.PUBLISH_EVENT) {
            updated.setState(EventState.PUBLISHED);
        } else if (request.getStateAction() == StateAdminAction.REJECT_EVENT) {
            updated.setState(EventState.CANCELED);
        }
        return mapper.mapEventToFullDto(updated);
    }
}
