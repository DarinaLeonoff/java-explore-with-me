package ru.practicum.ewm.event.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class EventShortDtoTest {
    @Autowired
    private JacksonTester<EventShortDto> jacksonTester;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void shouldSerializeEventShortDto() throws Exception {
        EventShortDto dto = EventShortDto.builder().annotation("annot").category(CategoryDto.builder().id(1).name("cat").build()).confirmedRequests(1).eventDate(LocalDateTime.now().plusDays(2)).id(2L).initiator(UserShortDto.builder().id(1L).name("Name").build()).paid(false).title("Title").views(20).build();

        JsonContent<EventShortDto> json = jacksonTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(json).extractingJsonPathStringValue("$.annotation").isEqualTo("annot");

        assertThat(json).extractingJsonPathNumberValue("$.category.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.category.name").isEqualTo("cat");

        assertThat(json).extractingJsonPathNumberValue("$.confirmedRequests").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.eventDate").isEqualTo(dto.getEventDate().format(formatter));

        assertThat(json).extractingJsonPathNumberValue("$.initiator.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.initiator.name").isEqualTo("Name");

        assertThat(json).extractingJsonPathBooleanValue("$.paid").isFalse();
        assertThat(json).extractingJsonPathStringValue("$.title").isEqualTo("Title");
        assertThat(json).extractingJsonPathNumberValue("$.views").isEqualTo(20);
    }

    @Test
    void shouldDeserializeEventShortDto() throws Exception {
        String json = "{\n" + "  \"id\": 2,\n" + "  \"annotation\": \"annot\",\n" + "  \"category\": {\n" + "    \"id\": 1,\n" + "    \"name\": \"cat\"\n" + "  },\n" + "  \"confirmedRequests\": 1,\n" + "  \"eventDate\": \"2026-02-11T12:00:00\",\n" + "  \"initiator\": {\n" + "    \"id\": 1,\n" + "    \"name\": \"Name\"\n" + "  },\n" + "  \"paid\": false,\n" + "  \"title\": \"Title\",\n" + "  \"views\": 20\n" + "}";

        EventShortDto dto = jacksonTester.parseObject(json);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getAnnotation()).isEqualTo("annot");
        assertThat(dto.getCategory().getId()).isEqualTo(1);
        assertThat(dto.getCategory().getName()).isEqualTo("cat");
        assertThat(dto.getConfirmedRequests()).isEqualTo(1);
        assertThat(dto.getEventDate()).isEqualTo(LocalDateTime.of(2026, 2, 11, 12, 0));
        assertThat(dto.getInitiator().getId()).isEqualTo(1L);
        assertThat(dto.getInitiator().getName()).isEqualTo("Name");
        assertThat(dto.getPaid()).isFalse();
        assertThat(dto.getTitle()).isEqualTo("Title");
        assertThat(dto.getViews()).isEqualTo(20);
    }
}
