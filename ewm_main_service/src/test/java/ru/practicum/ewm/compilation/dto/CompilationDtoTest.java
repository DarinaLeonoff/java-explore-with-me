package ru.practicum.ewm.compilation.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class CompilationDtoTest {
    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldSerializeCompilationDto() throws Exception {
        Event event = new Event();
        event.setId(1L);

        CompilationDto dto = CompilationDto.builder()
                .id(10L)
                .title("Compilation")
                .pinned(true)
                .events(List.of(event))
                .build();

        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"id\":10"));
        assertTrue(json.contains("\"title\":\"Compilation\""));
        assertTrue(json.contains("\"pinned\":true"));
        assertTrue(json.contains("\"events\""));
    }

    @Test
    void shouldDeserializeCompilationDto() throws Exception {
        String json = "{\n" +
                "  \"id\": 10,\n" +
                "  \"title\": \"Compilation\",\n" +
                "  \"pinned\": true,\n" +
                "  \"events\": []\n" +
                "}";

        CompilationDto dto = mapper.readValue(json, CompilationDto.class);

        assertEquals(10L, dto.getId());
        assertEquals("Compilation", dto.getTitle());
        assertTrue(dto.isPinned());
        assertNotNull(dto.getEvents());
        assertTrue(dto.getEvents().isEmpty());
    }

    @Test
    void shouldHandleEmptyEvents() throws Exception {
        CompilationDto dto = CompilationDto.builder()
                .id(1L)
                .title("Test")
                .pinned(false)
                .events(List.of())
                .build();

        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"events\":[]"));
    }


}
