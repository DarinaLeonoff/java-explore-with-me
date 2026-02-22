package ru.practicum.ewm.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.AccessRightsException;
import ru.practicum.ewm.exception.GlobalExceptionHandler;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventPrivateController.class)
@Import(GlobalExceptionHandler.class)
public class EventPrivateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateEvent() throws Exception {
        NewEventDto dto = NewEventDto.builder().annotation("Annotation text Annotation text Annotation text")
                .category(1).description("Description Text Description Text").eventDate(LocalDateTime.now().plusDays(2))
                .location(new Location()).paid(true).participantLimit(20).requestModeration(true).title("Title text")
                .build();

        EventFullDto result = new EventFullDto();

        when(eventService.userPostNewEvent(any(), eq(1L))).thenReturn(result);

        mockMvc.perform(post("/users/1/events").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated());

        verify(eventService).userPostNewEvent(any(), eq(1L));
    }

    @Test
    void shouldFailValidation() throws Exception {
        NewEventDto dto = new NewEventDto(); // пустой

        mockMvc.perform(post("/users/1/events").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUserEvents() throws Exception {
        List<EventShortDto> list = List.of(new EventShortDto());

        when(eventService.userGetUserEvents(1L, 0, 10)).thenReturn(list);

        mockMvc.perform(get("/users/1/events")).andExpect(status().isOk());

        verify(eventService).userGetUserEvents(1L, 0, 10);
    }

    @Test
    void shouldUsePaginationParams() throws Exception {
        mockMvc.perform(get("/users/1/events").param("from", "20").param("size", "5")).andExpect(status().isOk());

        verify(eventService).userGetUserEvents(1L, 20, 5);
    }

    @Test
    void shouldReturnEventById() throws Exception {
        when(eventService.userGetUserEventById(1L, 2L)).thenReturn(new EventFullDto());

        mockMvc.perform(get("/users/1/events/2")).andExpect(status().isOk());

        verify(eventService).userGetUserEventById(1L, 2L);
    }

    @Test
    void shouldUpdateEvent() throws Exception {
        UpdateEventUserRequest request = new UpdateEventUserRequest();

        when(eventService.userUpdateUserEvent(eq(1L), eq(2L), any())).thenReturn(new EventFullDto());

        mockMvc.perform(patch("/users/1/events/2").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());

        verify(eventService).userUpdateUserEvent(eq(1L), eq(2L), any());
    }

    @Test
    void shouldAcceptRequest() throws Exception {
        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(1L, 2L));
        request.setStatus(RequestState.CONFIRMED);

        when(requestService.acceptRequest(eq(1L), eq(2L), any())).thenReturn(Map.of());

        mockMvc.perform(patch("/users/1/events/2/requests").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());

        verify(requestService).acceptRequest(eq(1L), eq(2L), any());
    }

    @Test
    void shouldReturnRequests() throws Exception {
        when(requestService.getEventRequests(1L, 2L)).thenReturn(List.of());

        mockMvc.perform(get("/users/1/events/2/requests")).andExpect(status().isOk());

        verify(requestService).getEventRequests(1L, 2L);
    }

    @Test
    void shouldHandleAccessException() throws Exception {
        when(requestService.acceptRequest(anyLong(), anyLong(), any())).thenThrow(new AccessRightsException("error"));

        mockMvc.perform(patch("/users/1/events/2/requests").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
