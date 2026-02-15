package ru.practicum.ewm.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventStatsScheduler {
    private final EventRepository eventRepository;
    private final StatsClient client;

    @Scheduled(fixedDelay = 60000) // раз в минуту
    @Transactional
    public void updateViews() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by("publishedOn").descending());
        Page<Event> events = eventRepository.findByState(EventState.PUBLISHED, pageable);

        if (events.isEmpty()) {
            return;
        }

        List<String> uris = events.stream().map(e -> Constants.getEventUri(e.getId())).toList();


        LocalDateTime start = events.getContent().getFirst().getPublishedOn();
        LocalDateTime end = LocalDateTime.now();

        List<StatsResponseDto> stats = client.getStats(start, end, uris, true);

        Map<String, Long> viewsMap = stats.stream()
                .collect(Collectors.toMap(
                        StatsResponseDto::getUri,
                        StatsResponseDto::getHits
                ));

        events.forEach(event -> {
            Long views = viewsMap.getOrDefault(
                    Constants.getEventUri(event.getId()), 0L
            );
            event.setViews(views);
        });
        eventRepository.saveAll(events);
    }
}
