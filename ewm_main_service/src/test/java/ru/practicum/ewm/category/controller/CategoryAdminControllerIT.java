package ru.practicum.ewm.category.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryAdminControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDto category;

    private String baseUri = "/admin/categories";

    @BeforeEach
    void setup() throws Exception {
        NewCategoryDto categoryDto = new NewCategoryDto();
        categoryDto.setName("Music");

        String catResponse = mockMvc.perform(post("/admin/categories").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto))).andReturn().getResponse().getContentAsString();

        category = objectMapper.readValue(catResponse, CategoryDto.class);
    }

    @Test
    void shouldCreateCategory() throws Exception {
        NewCategoryDto dto = new NewCategoryDto();
        dto.setName("Theatre");

        mockMvc.perform(
                        post(baseUri).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Theatre"));
    }

    @Test
    void shouldUpdateCategory() throws Exception {
        category.setName("New");

        mockMvc.perform(patch(baseUri + "/" + category.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    void shouldDeleteCategory() throws Exception {
        NewCategoryDto dto = new NewCategoryDto();
        dto.setName("Delete");

        String resp = mockMvc.perform(
                        post(baseUri).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        CategoryDto created = objectMapper.readValue(resp, CategoryDto.class);

        mockMvc.perform(delete(baseUri + "/" + created.getId())).andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        CategoryDto dto = new CategoryDto();
        dto.setName("Fail");

        mockMvc.perform(patch(baseUri + "/999").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenNameBlank() throws Exception {
        NewCategoryDto dto = new NewCategoryDto();
        dto.setName("");

        mockMvc.perform(
                        post(baseUri).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
