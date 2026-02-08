package ru.practicum.ewm.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryPublicService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryPublicController.class)
public class CategoryPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryPublicService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCategoriesShouldReturnOk() throws Exception {
        when(service.getCategories(0, 10)).thenReturn(List.of(CategoryDto.builder().id(1).name("name 1").build(), CategoryDto.builder().id(2).name("name 2").build(), CategoryDto.builder().id(3).name("name 3").build()));

        mockMvc.perform(get("/categories")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getCategoryByIdShouldReturnOk() throws Exception {
        when(service.getCategory(1)).thenReturn(CategoryDto.builder().id(1).name("name 1").build());

        mockMvc.perform(get("/categories/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("name 1")).andExpect(jsonPath("$.id").value(1));
    }
}
