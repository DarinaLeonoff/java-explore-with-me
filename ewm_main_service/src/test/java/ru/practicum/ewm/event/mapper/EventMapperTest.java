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
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class EventMapperTest {
    @Autowired
    private EventMapper mapper;

    private Event event;

    @BeforeEach
    void setup() {
        event = Event.builder().id(1L).createdOn(LocalDateTime.now()).initiator(User.builder().id(1L).name("name").email("name@ya.ru").build()).title("title").annotation("Annotation").description("description").eventDate(LocalDateTime.now().plusDays(2)).category(Category.builder().id(1).name("Category").build()).paid(false).participantLimit(20).publishedOn(LocalDateTime.now()).requestModeration(false).confirmedRequests(5).state(EventState.PUBLISHED).location(Location.builder().lat(22.50).lon(50.20).build()).views(20).build();
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
}
