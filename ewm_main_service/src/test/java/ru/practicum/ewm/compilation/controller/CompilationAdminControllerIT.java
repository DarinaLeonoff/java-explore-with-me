package ru.practicum.ewm.compilation.controller;

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
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Location;
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
public class CompilationAdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto user;
    private EventFullDto event;
    private CategoryDto category;

    private String baseUri = "/admin/compilations";

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

    @Test
    void shouldCreateCompilation() throws Exception {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setTitle("Best events");
        dto.setPinned(true);
        dto.setEvents(List.of(event.getId()));

        mockMvc.perform(post("/admin/compilations").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Best events")).andExpect(jsonPath("$.events.length()").value(1));
    }

    @Test
    void shouldDeleteCompilation() throws Exception {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setTitle("Delete");
        dto.setEvents(List.of(event.getId()));

        String resp = mockMvc.perform(post("/admin/compilations").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andReturn().getResponse().getContentAsString();

        CompilationDto comp = objectMapper.readValue(resp, CompilationDto.class);

        mockMvc.perform(delete("/admin/compilations/" + comp.getId())).andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        UpdateCompilationDto update = new UpdateCompilationDto();
        update.setTitle("Fail");

        mockMvc.perform(patch("/admin/compilations/999").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update))).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest() throws Exception {
        NewCompilationDto dto = new NewCompilationDto(); // без title

        mockMvc.perform(post("/admin/compilations").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
    }
}
