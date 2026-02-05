package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import ru.practicum.dto.StatsResponseDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class StatsResponseDtoTest {
    private final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).build();

    String jsonString = "{\n" + "   \"app\":\"ewm-main-server\",\n" + "   \"uri\":\"/stats\",\n" + "   \"hits\":0\n" + "}";
    StatsResponseDto dto = StatsResponseDto.builder().app("ewm-main-server").uri("/stats").hits(0).build();

    @Test
    void testSerialization() throws IOException {
        String content = mapper.writeValueAsString(dto);

        assertThat(mapper.readTree(content)).isEqualTo(mapper.readTree(jsonString));
    }

    @Test
    void testDeserialization() throws IOException {
        StatsResponseDto content = mapper.readValue(jsonString, StatsResponseDto.class);

        assertThat(content.getApp()).isEqualTo(dto.getApp());
        assertThat(content.getUri()).isEqualTo(dto.getUri());
        assertThat(content.getHits()).isEqualTo(dto.getHits());
    }
}
