package ru.practicum.ewm.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.StateAdminAction;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventPublicControllerIT {

    @MockBean
    private StatsClient statsClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto user;
    private UserDto requester;
    private EventFullDto event;
    private CategoryDto category;

    private String baseUri = "/events";

    @BeforeEach
    void setup() throws Exception {
        // 1. создаём пользователя
        String userResponse = mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewUserRequestDto("User", "user@mail.com")))).andReturn()
                .getResponse().getContentAsString();

        user = objectMapper.readValue(userResponse, UserDto.class);

        String user2Response = mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewUserRequestDto("User2", "user222@mail.com"))))
                .andReturn().getResponse().getContentAsString();

        requester = objectMapper.readValue(user2Response, UserDto.class);

        // 3. создаём категорию
        NewCategoryDto categoryDto = new NewCategoryDto();
        categoryDto.setName("Music");

        String catResponse = mockMvc.perform(post("/admin/categories").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto))).andReturn().getResponse().getContentAsString();

        category = objectMapper.readValue(catResponse, CategoryDto.class);

        // 3. создаём событие
        NewEventDto newEventDto = NewEventDto.builder().annotation("Annotation Annotation Annotation Annotation")
                .category(category.getId()).description("description description description description")
                .eventDate(LocalDateTime.now().plusDays(3)).location(new Location(10D, 20D)).paid(true)
                .participantLimit(10).requestModeration(true).title("Title").build();
        String eventResponse = mockMvc.perform(
                        post("/users/" + user.getId() + "/events").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newEventDto))).andReturn().getResponse()
                .getContentAsString();

        event = objectMapper.readValue(eventResponse, EventFullDto.class);

        // 4. публикуем событие
        UpdateEventAdminRequest publishRequest = new UpdateEventAdminRequest();
        publishRequest.setStateAction(StateAdminAction.PUBLISH_EVENT);

        mockMvc.perform(patch("/admin/events/" + event.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publishRequest))).andExpect(status().isOk());
    }

    @Test
    void shouldReturnPublishedEvents() throws Exception {
        sendStatsMock();
        getStatsMock();
        mockMvc.perform(get(baseUri)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldFilterByCategory() throws Exception {
        sendStatsMock();
        getStatsMock();
        mockMvc.perform(get(baseUri).param("categories", String.valueOf(category.getId()))).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldFilterByPaid() throws Exception {
        sendStatsMock();
        getStatsMock();
        mockMvc.perform(get(baseUri).param("paid", "true")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get(baseUri).param("paid", "false")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldFilterByText() throws Exception {
        sendStatsMock();
        getStatsMock();
        mockMvc.perform(get(baseUri).param("text", "description")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnOnlyAvailableEvents() throws Exception {
        sendStatsMock();
        getStatsMock();
        mockMvc.perform(get(baseUri).param("onlyAvailable", "true")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldFilterByDateRange() throws Exception {
        sendStatsMock();
        getStatsMock();
        String start = LocalDateTime.now().plusDays(1).format(Constants.DATE_FORMATTER);

        String end = LocalDateTime.now().plusDays(5).format(Constants.DATE_FORMATTER);

        mockMvc.perform(get(baseUri).param("rangeStart", start).param("rangeEnd", end)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnEventById() throws Exception {
        sendStatsMock();
        getStatsMock();
        mockMvc.perform(get(baseUri + "/" + event.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()));
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        mockMvc.perform(get(baseUri + "/9999")).andExpect(status().isNotFound());
    }

    private void sendStatsMock() {
        doNothing().when(statsClient).hit(anyString(), anyString(), anyString());
    }

    private void getStatsMock() {
        when(statsClient.getStats(null, null, null, true)).thenReturn(List.of());
    }
}
