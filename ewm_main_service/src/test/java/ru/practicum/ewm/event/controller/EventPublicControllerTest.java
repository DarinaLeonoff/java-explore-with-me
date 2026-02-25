package ru.practicum.ewm.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.GlobalExceptionHandler;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventPublicController.class)
@Import(GlobalExceptionHandler.class)
public class EventPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnEventList() throws Exception {

        String ip = "127.0.0.1";

        EventShortDto dto = EventShortDto.builder().id(1L).title("Title").annotation("Annotation").paid(false)
                .views(10L).confirmedRequests(2).build();

        when(service.getPublicEventList(anyString(), any(), anyBoolean(), any(), any(), anyBoolean(), any(), anyInt(),
                anyInt(), anyString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/events").with(request -> {
                    request.setRemoteAddr(ip);
                    return request;
                }).param("text", "concert").param("paid", "false").param("from", "0").param("size", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].title").value("Title"))
                .andExpect(jsonPath("$[0].paid").value(false));

        verify(service).getPublicEventList(eq("concert"), isNull(), eq(false), isNull(), isNull(), eq(false), isNull(),
                eq(0), eq(10), eq(ip));
    }


    @Test
    void shouldReturnEventById() throws Exception {
        EventFullDto dto = EventFullDto.builder().id(1L).title("Full event").annotation("annotation").views(100L)
                .build();

        when(service.getPublicEvent(anyLong(), any())).thenReturn(dto);

        mockMvc.perform(get("/events/{id}", 1)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Full event")).andExpect(jsonPath("$.views").value(100));

        verify(service).getPublicEvent(anyLong(), any());
    }

    @Test
    void shouldUseDefaultFromAndSize() throws Exception {
        String ip = "127.0.0.1";
        when(service.getPublicEventList(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(),
                anyString())).thenReturn(List.of());

        mockMvc.perform(get("/events")).andExpect(status().isOk());

        verify(service).getPublicEventList(isNull(), isNull(), isNull(), isNull(), isNull(), eq(false), isNull(), eq(0),
                eq(10), eq(ip));
    }

    @ParameterizedTest
    @CsvSource({"-1, 10", "0, -5"})
    void shouldReturn400ForInvalidFromAndSize(int from, int size) throws Exception {
        mockMvc.perform(get("/events").param("from", String.valueOf(from)).param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }


}
