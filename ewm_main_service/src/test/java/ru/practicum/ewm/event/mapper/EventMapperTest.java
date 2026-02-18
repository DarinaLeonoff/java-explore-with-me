package ru.practicum.ewm.event.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EventMapperTest {
    @Autowired
    private EventMapper mapper;

    private Event event;

    @BeforeEach
    void setup() {
        event = Event.builder()
                .id(1L)
                .createdOn(LocalDateTime.now())
                .initiator(User.builder().id(1L)
                        .name("name")
                        .email("name@ya.ru").build())
                .title("title")
                .annotation("Annotation")
                .description("description")
                .eventDate(LocalDateTime.now().plusDays(2))
                .category(Category.builder().id(1).name("Category").build())
                .paid(false).participantLimit(20).publishedOn(LocalDateTime.now())
                .requestModeration(false).confirmedRequests(5)
                .state(EventState.PUBLISHED).location(Location.builder().lat(22.50).lon(50.20).build())
                .views(20L).build();
    }

    @Test
    void convertEventToShortDto() {
        EventShortDto dto = mapper.mapEventToShortDto(event);

        assertEquals(event.getAnnotation(), dto.getAnnotation());
        assertEquals(event.getCategory().getId(), dto.getCategory().getId());
        assertEquals(event.getAnnotation(), dto.getAnnotation());
        assertEquals(event.getConfirmedRequests(), dto.getConfirmedRequests());
        assertEquals(event.getEventDate(), dto.getEventDate());
        assertEquals(event.getId(), dto.getId());
        assertEquals(event.getInitiator().getId(), dto.getInitiator().getId());
        assertEquals(event.getPaid(), dto.getPaid());
        assertEquals(event.getTitle(), dto.getTitle());
        assertEquals(event.getViews(), dto.getViews());
    }

    @Test
    void convertEventToFulltDto() {
        EventFullDto dto = mapper.mapEventToFullDto(event);

        assertEquals(event.getAnnotation(), dto.getAnnotation());
        assertEquals(event.getCategory().getId(), dto.getCategory().getId());
        assertEquals(event.getConfirmedRequests(), dto.getConfirmedRequests());
        assertEquals(event.getCreatedOn(), dto.getCreatedOn());
        assertEquals(event.getDescription(), dto.getDescription());
        assertEquals(event.getEventDate(), dto.getEventDate());
        assertEquals(event.getId(), dto.getId());
        assertEquals(event.getInitiator().getId(), dto.getInitiator().getId());
        assertEquals(event.getLocation(), dto.getLocation());
        assertEquals(event.getPaid(), dto.getPaid());
        assertEquals(event.getParticipantLimit(), dto.getParticipantLimit());
        assertEquals(event.getPublishedOn(), dto.getPublishedOn());
        assertEquals(event.isRequestModeration(), dto.getRequestModeration());
        assertEquals(event.getState(), dto.getState());
        assertEquals(event.getTitle(), dto.getTitle());
        assertEquals(event.getViews(), dto.getViews());
    }

    @Test
    void convertNewEventToEvent() {
        NewEventDto dto = NewEventDto.builder().annotation("annotation text").category(1).description("description text").eventDate(LocalDateTime.now()).location(Location.builder().build()).paid(true).participantLimit(10).requestModeration(false).title("title").build();

        Event event1 = mapper.mapNewEventToEvent(dto,
                CategoryDto.builder().id(1).name("Category").build(),
                UserDto.builder().id(1L).name("name").email("name@ya.ru").build());

        assertNotNull(event1);

        assertEquals(event1.getAnnotation(), dto.getAnnotation());
        assertEquals(event1.getCategory().getId(), dto.getCategory());
        assertEquals(event1.getDescription(), dto.getDescription());
        assertEquals(event1.getEventDate(), dto.getEventDate());
        assertEquals(event1.getLocation(), dto.getLocation());
        assertEquals(event1.getPaid(), dto.isPaid());
        assertEquals(event1.getParticipantLimit(), dto.getParticipantLimit());
        assertEquals(event1.isRequestModeration(), dto.isRequestModeration());
        assertEquals(event1.getTitle(), dto.getTitle());
    }

    @Test
    void shouldUpdateAllFields() {

        Event event = baseEvent();

        UpdateEventRequest req = new UpdateEventRequest();
        req.setAnnotation("new annotation");
        req.setDescription("new description");
        req.setTitle("new title");
        req.setPaid(true);
        req.setParticipantLimit(100);
        req.setRequestModeration(true);
        req.setEventDate(LocalDateTime.now());
        req.setLocation(new Location(50.0, 60.0));

        Category newCategory = new Category();
        newCategory.setId(2);

        mapper.updateEvent(event, req, newCategory);

        assertEquals("new annotation", event.getAnnotation());
        assertEquals("new description", event.getDescription());
        assertEquals("new title", event.getTitle());
        assertTrue(event.getPaid());
        assertEquals(100, event.getParticipantLimit());
        assertTrue(event.isRequestModeration());
        assertEquals(2, event.getCategory().getId());
        assertEquals(50.0, event.getLocation().getLat());
    }

    @Test
    void shouldNotOverrideWithNulls() {

        Event event = baseEvent();

        UpdateEventRequest req = new UpdateEventRequest(); // все null

        mapper.updateEvent(event, req, null);

        assertEquals("old annotation", event.getAnnotation());
        assertEquals("old description", event.getDescription());
        assertEquals("old title", event.getTitle());
        assertFalse(event.getPaid());
        assertEquals(10, event.getParticipantLimit());
        assertFalse(event.isRequestModeration());
    }

    @Test
    void shouldUpdateOnlyAnnotation() {

        Event event = baseEvent();

        UpdateEventRequest req = new UpdateEventRequest();
        req.setAnnotation("updated");

        mapper.updateEvent(event, req, null);

        assertEquals("updated", event.getAnnotation());
        assertEquals("old title", event.getTitle());
    }

    @Test
    void shouldUpdateCategory() {

        Event event = baseEvent();

        Category newCategory = new Category();
        newCategory.setId(99);

        mapper.updateEvent(event, new UpdateEventRequest(), newCategory);

        assertEquals(99, event.getCategory().getId());
    }

    @Test
    void shouldUpdateLocation() {

        Event event = baseEvent();

        UpdateEventRequest req = new UpdateEventRequest();
        req.setLocation(new Location(1.0, 2.0));

        mapper.updateEvent(event, req, null);

        assertEquals(1.0, event.getLocation().getLat());
    }

    @Test
    void shouldReturnSameEventIfUpdatesNull() {

        Event event = baseEvent();

        Event result = mapper.updateEvent(event, null, null);

        assertSame(event, result);
    }

    private Event baseEvent() {
        Event event = new Event();

        event.setAnnotation("old annotation");
        event.setDescription("old description");
        event.setTitle("old title");
        event.setPaid(false);
        event.setParticipantLimit(10);
        event.setRequestModeration(false);

        Category category = new Category();
        category.setId(1);
        event.setCategory(category);

        Location location = new Location(10.0, 20.0);
        event.setLocation(location);

        return event;
    }
}
