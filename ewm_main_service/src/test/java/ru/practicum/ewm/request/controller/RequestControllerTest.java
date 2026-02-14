package ru.practicum.ewm.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.exception.notFound.RequestNotFound;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestPrivateService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestPrivateService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateRequest() throws Exception {
        ParticipationRequestDto dto = new ParticipationRequestDto();

        when(service.makeRequest(1L, 2L)).thenReturn(dto);

        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "2"))
                .andExpect(status().isCreated());

        verify(service).makeRequest(1L, 2L);
    }

    @Test
    void shouldFailWhenEventIdMissing() throws Exception {
        mockMvc.perform(post("/users/1/requests"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnUserRequests() throws Exception {
        List<ParticipationRequestDto> list =
                List.of(new ParticipationRequestDto());

        when(service.getUserRequests(1L)).thenReturn(list);

        mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk());

        verify(service).getUserRequests(1L);
    }

    @Test
    void shouldCancelRequest() throws Exception {
        ParticipationRequestDto dto = new ParticipationRequestDto();

        when(service.cancelRequest(1L, 2L)).thenReturn(dto);

        mockMvc.perform(patch("/users/1/requests/2/cancel"))
                .andExpect(status().isOk());

        verify(service).cancelRequest(1L, 2L);
    }

    @Test
    void shouldHandleNotFound() throws Exception {
        when(service.cancelRequest(anyLong(), anyLong()))
                .thenThrow(new RequestNotFound(2L));

        mockMvc.perform(patch("/users/1/requests/2/cancel"))
                .andExpect(status().isNotFound());
    }
}
