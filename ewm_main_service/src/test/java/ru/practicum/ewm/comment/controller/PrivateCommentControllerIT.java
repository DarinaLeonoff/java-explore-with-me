package ru.practicum.ewm.comment.controller;

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
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
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
public class PrivateCommentControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentDto comment;

    private String baseUri;

    @BeforeEach
    void setup() throws Exception {
        // 1. создаём пользователя
        String userResponse = mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewUserRequestDto("User", "user@mail.com")))).andReturn()
                .getResponse().getContentAsString();

        UserDto user = objectMapper.readValue(userResponse, UserDto.class);

        // 3. создаём категорию
        NewCategoryDto categoryDto = new NewCategoryDto();
        categoryDto.setName("Music");

        String catResponse = mockMvc.perform(post("/admin/categories").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto))).andReturn().getResponse().getContentAsString();

        CategoryDto category = objectMapper.readValue(catResponse, CategoryDto.class);

        // 3. создаём событие
        NewEventDto newEventDto = NewEventDto.builder().annotation("Annotation Annotation Annotation Annotation")
                .category(category.getId()).description("description description description description")
                .eventDate(LocalDateTime.now().plusDays(3)).location(new Location(10D, 20D)).paid(true)
                .participantLimit(10).requestModeration(true).title("Title").build();
        String eventResponse = mockMvc.perform(
                        post("/users/" + user.getId() + "/events").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newEventDto))).andReturn().getResponse()
                .getContentAsString();

        EventFullDto event = objectMapper.readValue(eventResponse, EventFullDto.class);

        // 4. публикуем событие
        UpdateEventAdminRequest publishRequest = new UpdateEventAdminRequest();
        publishRequest.setStateAction(StateAdminAction.PUBLISH_EVENT);

        mockMvc.perform(patch("/admin/events/" + event.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publishRequest))).andExpect(status().isOk());

        baseUri = "/users/" + user.getId() + "/events/" + event.getId() + "/comments";

        //5. создаем комментарий
        NewCommentDto dto = NewCommentDto.builder().text("comment").build();
        String commentResponse = mockMvc.perform(
                        post(baseUri).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();
        comment = objectMapper.readValue(commentResponse, CommentDto.class);
    }

    @Test
    void shouldSaveAndReturnComment() throws Exception {
        NewCommentDto dto = NewCommentDto.builder().text("comment").build();
        mockMvc.perform(
                        post(baseUri).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.text").value("comment"));
    }

    @Test
    void shouldUpdateCommentAndReturn() throws Exception {
        UpdateCommentDto dto = UpdateCommentDto.builder().text("update").build();
        mockMvc.perform(patch(baseUri + "/" + comment.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
                .andExpect(jsonPath("$" + ".text").value("update")).andExpect(jsonPath("$.id").value(comment.getId()));
    }

    @Test
    void shouldDeleteComment() throws Exception {
        mockMvc.perform(delete(baseUri + "/" + comment.getId())).andExpect(status().isNoContent());
    }
}
