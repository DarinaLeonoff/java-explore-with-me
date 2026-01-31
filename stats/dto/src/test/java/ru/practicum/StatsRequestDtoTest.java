package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import ru.practicum.dto.StatsRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


public class StatsRequestDtoTest {
    private final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).build();

    String jsonString = "{\n" + "   \"app\":\"ewm-main-server\",\n" + "   \"uri\":\"/hit\",\n" + "   \"ip\":\"192.163.0.1\",\n" + "   \"created\":\"2022-09-06T11:00:23\"\n" + "}";
    LocalDateTime createDT = LocalDateTime.parse("2022-09-06T11:00:23");
    StatsRequestDto dto = StatsRequestDto.builder().app("ewm-main-server").uri("/hit").ip("192.163.0.1").created(createDT).build();

    @Test
    void testSerialization() throws IOException {
        String content = mapper.writeValueAsString(dto);

        assertThat(mapper.readTree(content)).isEqualTo(mapper.readTree(jsonString));
    }

    @Test
    void testDeserialization() throws IOException {
        StatsRequestDto content = mapper.readValue(jsonString, StatsRequestDto.class);

        assertThat(content.getIp()).isEqualTo(dto.getIp());
        assertThat(content.getApp()).isEqualTo(dto.getApp());
        assertThat(content.getUri()).isEqualTo(dto.getUri());
        assertThat(content.getCreated()).isEqualTo(dto.getCreated());
    }
}
