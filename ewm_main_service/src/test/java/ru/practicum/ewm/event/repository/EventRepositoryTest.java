package ru.practicum.ewm.event.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EventRepositoryTest {
        @Autowired
        private EventRepository eventRepository;

        @Autowired
        private TestEntityManager em;


    private Category music;
    private Category sport;
    private User initiator;

    private Event availablePaidEvent;
    private Event unavailableFreeEvent;

    @BeforeEach
    void setUp() {
        music = em.persist(
                Category.builder()
                        .name("Music")
                        .build()
        );

        sport = em.persist(
                Category.builder()
                        .name("Sport")
                        .build()
        );

        initiator = em.persist(User.builder().name("User for test").email("name@ya.ru").build());

        availablePaidEvent = em.persist(
                Event.builder()
                        .annotation("Great concert")
                        .description("Live music event")
                        .title("Title")
                        .paid(true)
                        .category(music)
                        .initiator(initiator)
                        .eventDate(LocalDateTime.now().plusDays(10))
                        .participantLimit(100)
                        .confirmedRequests(10)
                        .state(EventState.PUBLISHED)
                        .location(Location.builder().lon(22.2).lat(55.3).build())
                        .build()
        );

        unavailableFreeEvent = em.persist(
                Event.builder()
                        .annotation("Morning run")
                        .description("Free sport event")
                        .title("Title")
                        .paid(false)
                        .initiator(initiator)
                        .category(sport)
                        .eventDate(LocalDateTime.now().plusDays(5))
                        .participantLimit(10)
                        .confirmedRequests(10)
                        .state(EventState.PUBLISHED)
                        .location(Location.builder().lon(22.2).lat(55.3).build())
                        .build()
        );

        em.flush();
        em.clear();
    }

    @Test
    void shouldFindByTextInAnnotationOrDescription() {
        Page<Event> result = eventRepository.getEventsByFilters(
                "concert",
                null,
                null,
                null,
                null,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(availablePaidEvent.getId(), result.getContent().get(0).getId());
    }

    @Test
    void shouldFilterByPaid() {
        Page<Event> result = eventRepository.getEventsByFilters(
                null,
                null,
                true,
                null,
                null,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getPaid());
    }

    @Test
    void shouldFilterByCategories() {
        Page<Event> result = eventRepository.getEventsByFilters(
                null,
                List.of(music.getId().intValue()),
                null,
                null,
                null,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(music.getId(), result.getContent().get(0).getCategory().getId());
    }

    @Test
    void shouldFilterByDateRange() {
        Page<Event> result = eventRepository.getEventsByFilters(
                null,
                null,
                null,
                LocalDateTime.now().plusDays(6),
                LocalDateTime.now().plusDays(15),
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(availablePaidEvent.getId(), result.getContent().get(0).getId());
    }

    @Test
    void shouldReturnOnlyAvailableEvents() {
        Page<Event> result = eventRepository.getEventsByFilters(
                null,
                null,
                null,
                null,
                null,
                true,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(availablePaidEvent.getId(), result.getContent().get(0).getId());
    }
}
