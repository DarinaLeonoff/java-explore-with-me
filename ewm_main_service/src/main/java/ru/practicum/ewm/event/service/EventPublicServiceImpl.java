package ru.practicum.ewm.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.EventSpecification;
import ru.practicum.ewm.exception.IllegalStateException;
import ru.practicum.ewm.exception.notFound.EventNotFound;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
            String rangeEnd, Boolean onlyAvailable, SortType sort, int from, int size, String ip) {

        //получение корректной даты
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, Constants.DATE_FORMATTER);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, Constants.DATE_FORMATTER);

        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalStateException("rangeStart must be before rangeEnd");
        }

        // фиксация просмотра страницы
        client.hit(Constants.APP, "/events", ip);

        //получения списка событий по старым views
        Specification<Event> spec = EventSpecification.withPublicFilters(text, categories, paid, start, end,
                onlyAvailable);
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(sort == SortType.VIEWS ? "views" : "eventDate").descending());
        Page<Event> res = repository.findAll(spec, pageable);

        //обновление просмотров
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

        return res.stream().map(mapper::mapEventToShortDto).toList();
    }

    @Override
    public EventFullDto getEvent(long id, String ip) throws InterruptedException {

        Event event = null;

        for (int i = 0; i < 5; i++) {
            Optional<Event> opt = repository.findByIdAndState(id, EventState.PUBLISHED);
            if (opt.isPresent()) {
                event = opt.get();
                break;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }

        if (event == null) {
            throw new EventNotFound(id);
        }

        client.hit(Constants.APP, Constants.getEventUri(id), ip);

        List<StatsResponseDto> stats = client.getStats(event.getCreatedOn(), LocalDateTime.now(),
                List.of(Constants.getEventUri(id)), true);

        if (!stats.isEmpty()) {
            event.setViews(stats.getFirst().getHits());
            //кеширование просмотров
            repository.save(event);
        }

        return mapper.mapEventToFullDto(event);
    }

}
