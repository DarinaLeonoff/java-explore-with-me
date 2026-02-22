package ru.practicum.ewm.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryAdminController.class)
public class CategoryAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void postShouldReturnCreated() throws Exception {
        NewCategoryDto dto = new NewCategoryDto();
        dto.setName("Category");
        CategoryDto categoryDto = CategoryDto.builder().id(1).name(dto.getName()).build();

        when(service.adminCreateCategory(any(NewCategoryDto.class))).thenReturn(categoryDto);

        mockMvc.perform(post("/admin/categories").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));
    }

    @Test
    void editShouldReturnOk() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().id(1).name("adit category").build();

        when(service.adminUpdateCategory(any(CategoryDto.class), anyInt())).thenReturn(categoryDto);

        mockMvc.perform(patch("/admin/categories/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto))).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));
    }

    @Test
    void postShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/admin/categories/1")).andExpect(status().isNoContent());

        verify(service, times(1)).adminRemoveCategory(anyInt());
    }

}
