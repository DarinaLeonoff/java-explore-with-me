package ru.practicum.ewm.compilation.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class UpdateCompilationTest {

    @Autowired
    private ObjectMapper mapper;

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void updateTitleTest() {
        UpdateCompilationDto dto = UpdateCompilationDto.builder().title("title").build();

        assertEquals("title", dto.getTitle());
        assertNull(dto.getPinned());
        assertNull(dto.getEvents());
    }

    @Test
    void updatePinnedTest() {
        UpdateCompilationDto dto = UpdateCompilationDto.builder().pinned(true).build();

        assertEquals(dto.getPinned(), true);
        assertNull(dto.getTitle());
        assertNull(dto.getEvents());
    }

    @Test
    void updateEventsTest() {
        UpdateCompilationDto dto = UpdateCompilationDto.builder().events(List.of(1L, 2L)).build();

        assertEquals(2, dto.getEvents().size());
        assertNull(dto.getTitle());
        assertNull(dto.getPinned());
    }

    @ParameterizedTest
    @EmptySource
    void validationInvalidTitleTest(String title) {
        UpdateCompilationDto dto = UpdateCompilationDto.builder().title(title).build();

        Set<ConstraintViolation<UpdateCompilationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldSerializeDto() throws Exception {
        UpdateCompilationDto dto = new UpdateCompilationDto();
        dto.setTitle("Test title");
        dto.setPinned(true);
        dto.setEvents(List.of(1L, 2L));

        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"title\":\"Test title\""));
        assertTrue(json.contains("\"pinned\":true"));
        assertTrue(json.contains("\"events\":[1,2]"));
    }

    @Test
    void shouldDeserializeDto() throws Exception {
        String json = "{\n" + "  \"title\": \"Test title\",\n" + "  \"pinned\": true,\n" + "  \"events\": [1, 2]\n" + "}";

        UpdateCompilationDto dto = mapper.readValue(json, UpdateCompilationDto.class);

        assertEquals("Test title", dto.getTitle());
        assertTrue(dto.getPinned());
        assertEquals(List.of(1L, 2L), dto.getEvents());
    }

    @Test
    void shouldDeserializeDtoWithotList() throws Exception {
        String json = "{\n" + "  \"title\": \"Test title\",\n" + "  \"pinned\": true\n" + "}";

        UpdateCompilationDto dto = mapper.readValue(json, UpdateCompilationDto.class);

        assertEquals("Test title", dto.getTitle());
        assertTrue(dto.getPinned());
        assertNull(dto.getEvents());
    }

}
