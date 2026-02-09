package ru.practicum.ewm.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.notFound.EventNotFound;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventPublicServiceImpl implements EventPublicService {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventMapper mapper;
    @Autowired
    private StatsClient client;

    @Override
    public List<EventShortDto> getEventList(String text, List<Integer> categories, Boolean paid, String rangeStart,
            String rangeEnd, Boolean onlyAvailable, SortType sort, int from, int size) {

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(sort == SortType.VIEWS ? "views" : "eventDate").descending());
        Page<Event> res = repository.getEventsByFilters(text, categories, paid, rangeStart != null ? LocalDateTime.parse(rangeStart) : null, rangeEnd != null ? LocalDateTime.parse(rangeEnd) : null, onlyAvailable.equals(true), pageable);
        //todo get views
        return res.stream().map(mapper::mapEventToShortDto).toList();
    }

    @Override
    public EventFullDto getEvent(long id) {
        // todo set stat
        return mapper.mapEventToFullDto(repository.findById(id).orElseThrow(() -> new EventNotFound(id)));
    }

    //todo get stats
}
