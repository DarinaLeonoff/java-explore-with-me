package ru.practicum.stats.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.stats.server.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatService service;

    @Autowired
    private ObjectMapper objectMapper;

    //hit tests
    @Test
    void hitShouldReturnOk() throws Exception {
        StatsRequestDto dto = StatsRequestDto.builder().app("app").uri("/hit").ip("127.0.0.1").timestamp(LocalDateTime.now()).build();

        mockMvc.perform(post("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(service, times(1)).hit(any(StatsRequestDto.class));
    }

    @Test
    void hitShouldReturnBadRequestWhenInvalidIp() throws Exception {
        StatsRequestDto dto = StatsRequestDto.builder().app("app").uri("/hit").ip("999.999.999.999").timestamp(LocalDateTime.now()).build();

        mockMvc.perform(post("/hit").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void hitShouldReturnBadRequestWhenAppIsNull() throws Exception {
        StatsRequestDto dto = StatsRequestDto.builder().uri("/hit").ip("127.0.0.1").timestamp(LocalDateTime.now()).build();

        mockMvc.perform(post("/hit").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    //    get tests
    @Test
    void getStats_withAllParams() throws Exception {
        List<StatsResponseDto> response = List.of(new StatsResponseDto("app", "/hit", 2L));

        when(service.getStats(any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/stats").param("start", "2022-09-01 00:00:00").param("end", "2022-09-30 23:59:59").param("uris", "/hit", "/view").param("unique", "true")).andExpect(status().isOk()).andExpect(jsonPath("$[0].app").value("app")).andExpect(jsonPath("$[0].uri").value("/hit")).andExpect(jsonPath("$[0].hits").value(2));

        verify(service).getStats(any(), any(), any(), eq(true));
    }

    @Test
    void getStats_shouldReturnBadRequest_whenStartMissing() throws Exception {
        mockMvc.perform(get("/stats").param("end", "2022-09-30 23:59:59")).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
