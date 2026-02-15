package ru.practicum.ewm.compilation.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class NewCompilationTest {

    @Autowired
    private ObjectMapper mapper;

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDtoTest() {
        NewCompilationDto dto = NewCompilationDto.builder().title("title").build();

        assertEquals("title", dto.getTitle());
        assertFalse(dto.isPinned());
        assertTrue(dto.getEvents().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validationInvalidTitleTest(String title) {
        NewCompilationDto dto = NewCompilationDto.builder().title(title).build();

        Set<ConstraintViolation<NewCompilationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldSerializeDto() throws Exception {
        NewCompilationDto dto = new NewCompilationDto();
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

        NewCompilationDto dto = mapper.readValue(json, NewCompilationDto.class);

        assertEquals("Test title", dto.getTitle());
        assertTrue(dto.isPinned());
        assertEquals(List.of(1L, 2L), dto.getEvents());
    }

}
