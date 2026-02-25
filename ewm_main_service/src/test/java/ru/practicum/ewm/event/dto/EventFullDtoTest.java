package ru.practicum.ewm.event.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class EventFullDtoTest {
    @Autowired
    private JacksonTester<EventFullDto> tester;

    @Test
    void shouldSerializeEventFullDto() throws Exception {
        LocalDateTime createdOn = LocalDateTime.of(2026, 2, 1, 10, 0);
        LocalDateTime eventDate = LocalDateTime.of(2026, 3, 10, 18, 30);
        LocalDateTime publishedOn = LocalDateTime.of(2026, 2, 5, 12, 0);

        EventFullDto dto = EventFullDto.builder().id(1L).annotation("annotation").category(CategoryDto.builder().id(10).name("category").build()).confirmedRequests(5).createdOn(createdOn).description("description").eventDate(eventDate).initiator(UserShortDto.builder().id(2L).name("initiator").build()).location(new Location(55.75, 37.61)).paid(true).participantLimit(100).publishedOn(publishedOn).requestModeration(true).state(EventState.PUBLISHED).title("title").views(42L).build();

        JsonContent<EventFullDto> json = tester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.annotation").isEqualTo("annotation");

        assertThat(json).extractingJsonPathNumberValue("$.category.id").isEqualTo(10);
        assertThat(json).extractingJsonPathStringValue("$.category.name").isEqualTo("category");

        assertThat(json).extractingJsonPathNumberValue("$.confirmedRequests").isEqualTo(5);
        assertThat(json).extractingJsonPathStringValue("$.createdOn").isEqualTo("2026-02-01 10:00:00");

        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(json).extractingJsonPathStringValue("$.eventDate").isEqualTo("2026-03-10 18:30:00");

        assertThat(json).extractingJsonPathNumberValue("$.initiator.id").isEqualTo(2);
        assertThat(json).extractingJsonPathStringValue("$.initiator.name").isEqualTo("initiator");

        assertThat(json).extractingJsonPathNumberValue("$.location.lat").isEqualTo(55.75);
        assertThat(json).extractingJsonPathNumberValue("$.location.lon").isEqualTo(37.61);

        assertThat(json).extractingJsonPathBooleanValue("$.paid").isTrue();
        assertThat(json).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(100);

        assertThat(json).extractingJsonPathStringValue("$.publishedOn").isEqualTo("2026-02-05 12:00:00");

        assertThat(json).extractingJsonPathBooleanValue("$.requestModeration").isTrue();
        assertThat(json).extractingJsonPathStringValue("$.state").isEqualTo("PUBLISHED");
        assertThat(json).extractingJsonPathStringValue("$.title").isEqualTo("title");
        assertThat(json).extractingJsonPathNumberValue("$.views").isEqualTo(42);
    }

    @Test
    void shouldDeserializeEventFullDto() throws Exception {
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"annotation\": \"annotation\",\n" +
                "  \"category\": {\n" +
                "    \"id\": 10,\n" +
                "    \"name\": \"category\"\n" +
                "  },\n" +
                "  \"confirmedRequests\": 5,\n" +
                "  \"createdOn\": \"2026-02-01 10:00:00\",\n" +
                "  \"description\": \"description\",\n" +
                "  \"eventDate\": \"2026-03-10 18:30:00\",\n" +
                "  \"initiator\": {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"initiator\"\n" +
                "  },\n" +
                "  \"location\": {\n" +
                "    \"lat\": 55.75,\n" +
                "    \"lon\": 37.61\n" +
                "  },\n" +
                "  \"paid\": true,\n" +
                "  \"participantLimit\": 100,\n" +
                "  \"publishedOn\": \"2026-02-05 12:00:00\",\n" +
                "  \"requestModeration\": true,\n" +
                "  \"state\": \"PUBLISHED\",\n" +
                "  \"title\": \"title\",\n" +
                "  \"views\": 42\n" +
                "}";

        EventFullDto dto = tester.parseObject(json);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getAnnotation()).isEqualTo("annotation");
        assertThat(dto.getCategory().getName()).isEqualTo("category");
        assertThat(dto.getConfirmedRequests()).isEqualTo(5);
        assertThat(dto.getCreatedOn()).isEqualTo(LocalDateTime.of(2026, 2, 1, 10, 0));
        assertThat(dto.getEventDate()).isEqualTo(LocalDateTime.of(2026, 3, 10, 18, 30));
        assertThat(dto.getInitiator().getName()).isEqualTo("initiator");
        assertThat(dto.getLocation().getLat()).isEqualTo(55.75);
        assertThat(dto.getPaid()).isTrue();
        assertThat(dto.getParticipantLimit()).isEqualTo(100);
        assertThat(dto.getRequestModeration()).isTrue();
        assertThat(dto.getState()).isEqualTo(EventState.PUBLISHED);
        assertThat(dto.getTitle()).isEqualTo("title");
        assertThat(dto.getViews()).isEqualTo(42);
    }
}
