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
public class CategoryPublicControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDto category;

    private String baseUri = "/categories";

    @BeforeEach
    void setup() throws Exception {
        NewCategoryDto categoryDto = new NewCategoryDto();
        categoryDto.setName("Music");

        String catResponse = mockMvc.perform(post("/admin/categories").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto))).andReturn().getResponse().getContentAsString();

        category = objectMapper.readValue(catResponse, CategoryDto.class);
    }

    @Test
    void shouldReturnCategories() throws Exception {
        mockMvc.perform(get(baseUri)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnCategoryById() throws Exception {
        mockMvc.perform(get(baseUri + "/" + category.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId())).andExpect(jsonPath("$.name").value("Music"));
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        mockMvc.perform(get(baseUri + "/999")).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(delete("/admin/categories/" + category.getId())).andExpect(status().isNoContent());

        mockMvc.perform(get(baseUri)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }
}
