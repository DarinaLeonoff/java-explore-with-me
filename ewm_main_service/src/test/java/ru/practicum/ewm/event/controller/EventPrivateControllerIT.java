package ru.practicum.ewm.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.StateAdminAction;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventPrivateControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto user;
    private UserDto requester;
    private EventFullDto event;
    private CategoryDto category;

    private String baseUri;

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

        baseUri = "/users/" + user.getId() + "/events";
    }

    @Test
    void shouldCreateEvent() throws Exception {
        NewEventDto dto = NewEventDto.builder().annotation("annotation annotation annotation annotation")
                .description("description description description description").category(category.getId())
                .eventDate(LocalDateTime.now().plusDays(3)).location(new Location(10D, 20D)).paid(false)
                .title("New event").build();

        mockMvc.perform(post("/users/" + user.getId() + "/events").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated());
    }

    @Test
    void shouldReturnUserEvents() throws Exception {
        mockMvc.perform(get("/users/" + user.getId() + "/events")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnUserEventById() throws Exception {
        mockMvc.perform(get("/users/" + user.getId() + "/events/" + event.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()));
    }

    @Test
    void shouldUpdateUserEvent() throws Exception {
        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setTitle("Updated title");

        mockMvc.perform(
                        patch("/users/" + user.getId() + "/events/" + event.getId()).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"));
    }

    @Test
    void shouldReturnRequestsForEvent() throws Exception {
        publish();
        // requester создаёт запрос
        mockMvc.perform(post("/users/" + requester.getId() + "/requests").param("eventId", event.getId().toString()))
                .andExpect(status().isCreated());

        // организатор получает список
        mockMvc.perform(get("/users/" + user.getId() + "/events/" + event.getId() + "/requests"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldConfirmRequest() throws Exception {
        publish();
        // создаём заявку
        String response = mockMvc.perform(
                        post("/users/" + requester.getId() + "/requests").param("eventId", event.getId().toString()))
                .andReturn().getResponse().getContentAsString();

        ParticipationRequestDto req = objectMapper.readValue(response, ParticipationRequestDto.class);

        EventRequestStatusUpdateRequest update = new EventRequestStatusUpdateRequest();
        update.setRequestIds(List.of(req.getId()));
        update.setStatus(RequestState.CONFIRMED);

        mockMvc.perform(patch("/users/" + user.getId() + "/events/" + event.getId() + "/requests").contentType(
                        MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(update))).andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests.length()").value(1));
    }

    @Test
    void shouldReturnConflictWhenUpdatingPublishedEvent() throws Exception {
        publish();
        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setTitle("fail");

        mockMvc.perform(
                patch("/users/" + user.getId() + "/events/" + event.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict());
    }

    private void publish() throws Exception {
        UpdateEventAdminRequest publishRequest = new UpdateEventAdminRequest();
        publishRequest.setStateAction(StateAdminAction.PUBLISH_EVENT);

        mockMvc.perform(patch("/admin/events/" + event.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publishRequest))).andExpect(status().isOk());
    }
}
