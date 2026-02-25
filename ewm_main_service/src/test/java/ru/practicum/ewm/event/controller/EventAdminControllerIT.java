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
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventAdminControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto user;
    private EventFullDto event;
    private CategoryDto category;

    private String baseUri = "/admin/events";

    @BeforeEach
    void setup() throws Exception {
        // 1. создаём пользователя
        String userResponse = mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewUserRequestDto("User", "user@mail.com")))).andReturn()
                .getResponse().getContentAsString();

        user = objectMapper.readValue(userResponse, UserDto.class);

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

    }

    // ================= GET =================

    @Test
    void shouldReturnEvents() throws Exception {
        mockMvc.perform(get("/admin/events")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldFilterByUser() throws Exception {
        mockMvc.perform(get("/admin/events").param("users", user.getId().toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].initiator.id").value(user.getId()));
    }

    @Test
    void shouldFilterByState() throws Exception {
        mockMvc.perform(get("/admin/events").param("states", "PENDING")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].state").value("PENDING"));
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() throws Exception {
        mockMvc.perform(get("/admin/events").param("states", "PUBLISHED")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ================= PATCH =================

    @Test
    void shouldPublishEvent() throws Exception {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAdminAction.PUBLISH_EVENT);

        mockMvc.perform(patch("/admin/events/" + event.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("PUBLISHED"));
    }

    @Test
    void shouldRejectEvent() throws Exception {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAdminAction.REJECT_EVENT);

        mockMvc.perform(patch("/admin/events/" + event.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("CANCELED"));
    }

    @Test
    void shouldReturnConflictWhenPublishingNotPending() throws Exception {
        // сначала публикуем
        UpdateEventAdminRequest publish = new UpdateEventAdminRequest();
        publish.setStateAction(StateAdminAction.PUBLISH_EVENT);

        mockMvc.perform(patch("/admin/events/" + event.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publish)));

        // повторная публикация
        mockMvc.perform(patch("/admin/events/" + event.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publish))).andExpect(status().isConflict());
    }

}
