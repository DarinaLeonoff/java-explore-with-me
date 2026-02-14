package ru.practicum.ewm.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventAdminService;
import ru.practicum.ewm.exception.GlobalExceptionHandler;
import ru.practicum.ewm.exception.notFound.EventNotFound;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventAdminController.class)
@Import(GlobalExceptionHandler.class)
public class EventAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventAdminService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnEventsForAdmin() throws Exception {
        List<EventFullDto> list = List.of(new EventFullDto());

        when(service.getEvent(any(), any(), any(), any(), any(), eq(0), eq(10)))
                .thenReturn(list);

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk());

        verify(service).getEvent(any(), any(), any(), any(), any(), eq(0), eq(10));
    }

    @Test
    void shouldPassFiltersToService() throws Exception {
        mockMvc.perform(get("/admin/events")
                        .param("users", "1", "2")
                        .param("states", "PUBLISHED")
                        .param("categories", "5")
                        .param("rangeStart", "2025-01-01 10:00:00")
                        .param("rangeEnd", "2025-01-02 10:00:00")
                        .param("from", "20")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(service).getEvent(
                eq(List.of(1L, 2L)),
                eq(List.of("PUBLISHED")),
                eq(List.of(5)),
                eq("2025-01-01 10:00:00"),
                eq("2025-01-02 10:00:00"),
                eq(20),
                eq(5)
        );
    }

    @Test
    void shouldUseDefaultPagination() throws Exception {
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk());

        verify(service).getEvent(any(), any(), any(), any(), any(), eq(0), eq(10));
    }

    @Test
    void shouldUpdateEvent() throws Exception {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();

        when(service.updateEvent(eq(1L), any()))
                .thenReturn(new EventFullDto());

        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).updateEvent(eq(1L), any());
    }

    @Test
    void shouldHandleNotFound() throws Exception {
        when(service.updateEvent(any(), any()))
                .thenThrow(new EventNotFound(1L));

        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

}
