package ru.practicum.ewm.event.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0 / 10, 10, Sort.by("id").ascending());

        music = em.persist(Category.builder().name("Music").build());

        sport = em.persist(Category.builder().name("Sport").build());

        initiator = em.persist(User.builder().name("User for test").email("name@ya.ru").build());

        availablePaidEvent = em.persist(Event.builder().createdOn(LocalDateTime.now()).initiator(initiator).title("Rock Concert").annotation("Great music concert").description("Big open air rock concert in the city center").eventDate(LocalDateTime.now().plusDays(10)).category(music).paid(true).participantLimit(100).confirmedRequests(10).requestModeration(true).state(EventState.PUBLISHED).location(new Location(55.75, 37.61)).views(0L).build());

        unavailableFreeEvent = em.persist(Event.builder().createdOn(LocalDateTime.now()).initiator(initiator).title("Local Football Match").annotation("Sport event").description("Football championship match").eventDate(LocalDateTime.now().plusDays(5)).category(sport).paid(false).participantLimit(10).confirmedRequests(10)
                .requestModeration(false).state(EventState.PUBLISHED).location(new Location(59.93, 30.31)).views(0L).build());

        em.flush();
        em.clear();
    }

    @Test
    void getEventByUserAndId() {
        Event eve = eventRepository.findByIdAndInitiatorId(availablePaidEvent.getId(), initiator.getId());

        assertEquals(availablePaidEvent.getId(), eve.getId());
    }

    @Test
    void getEventByUser() {
        Page<Event> eves = eventRepository.findAllByInitiatorId(initiator.getId(), pageable);

        assertEquals(eves.getTotalElements(), 2L);
    }

    @Test
    void getEventByState() {
        Page<Event> eves = eventRepository.findByState(EventState.PUBLISHED, pageable);

        assertEquals(eves.getTotalElements(), 2L);
    }

    //filters test

    @Test
    void shouldFilterByDateRange() {

        Specification<Event> spec =
                EventSpecification.withPublicFilters(
                        null, null, null,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(6),
                        null
                );

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(1, result.size());
    }

    @Test
    void shouldFilterByText() {
        Specification<Event> spec =
                EventSpecification.withPublicFilters(
                        "music", null, null, null, null, null
                );

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(1, result.size());
        assertEquals("Great music concert", result.get(0).getAnnotation());
    }

    @Test
    void shouldFilterByCategory() {

        Specification<Event> spec =
                EventSpecification.withPublicFilters(
                        null, List.of(music.getId()), null, null, null, null
                );

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(1, result.size());
        assertEquals(result.getFirst().getCategory().getName(), "Music");
    }

    @Test
    void shouldFilterByEmptyCategory() {

        Specification<Event> spec =
                EventSpecification.withPublicFilters(
                        null, List.of(), null, null, null, null
                );

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFilterByPaid() {
        Specification<Event> spec =
                EventSpecification.withPublicFilters(
                        null, null, true, null, null, null
                );

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getPaid());
    }

    @Test
    void shouldFilterOnlyAvailable() {
        Specification<Event> spec =
                EventSpecification.withPublicFilters(
                        null, null, null, null, null, true
                );

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(1, result.size());
    }

    //Admin filters
    @Test
    void shouldFilterByUsers() {
        Specification<Event> spec =
                EventSpecification.withAdminFilters(
                        List.of(initiator.getId()), null, null, null, null);

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(e -> e.getInitiator().getId().equals(initiator.getId())));

    }

    @Test
    void shouldFilterByEmptyUsers() {
        Specification<Event> spec =
                EventSpecification.withAdminFilters(
                        List.of(), null, null, null, null);

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFilterByStates() {
        Specification<Event> spec =
                EventSpecification.withAdminFilters(
                        null, List.of(EventState.PUBLISHED.name()), null, null, null);

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFilterByEmptyStates() {
        Specification<Event> spec =
                EventSpecification.withAdminFilters(
                        null, List.of(), null, null, null);

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFilterByUserAndState() {
        Specification<Event> spec =
                EventSpecification.withAdminFilters(
                        List.of(initiator.getId()),
                        List.of(EventState.PUBLISHED.name()),
                        null,
                        null,
                        null);

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFilterAdminByCategoryAndState() {
        Specification<Event> spec =
                EventSpecification.withAdminFilters(
                        null,
                        List.of(EventState.PUBLISHED.name()),
                        List.of(music.getId()),
                        null,
                        null);

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(1, result.size());
    }

    @Test
    void shouldFilterAdminByDateRange() {
        Specification<Event> spec =
                EventSpecification.withAdminFilters(
                        null,
                        List.of(EventState.PUBLISHED.name()),
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(6));

        List<Event> result = eventRepository.findAll(spec);

        assertEquals(1, result.size());
    }


}
