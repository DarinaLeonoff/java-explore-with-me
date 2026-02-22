package ru.practicum.ewm.compilation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.exception.notFound.CompilationNotFound;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompilationAdminController.class)
public class CompilationAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCompilation() throws Exception {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setTitle("Test");

        CompilationDto result = new CompilationDto();
        result.setId(1L);

        when(service.adminCreateCompilation(any())).thenReturn(result);

        mockMvc.perform(post("/admin/compilations").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(service).adminCreateCompilation(any());
    }

    @Test
    void shouldFailValidationWhenTitleMissing() throws Exception {
        NewCompilationDto dto = new NewCompilationDto();

        mockMvc.perform(post("/admin/compilations").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldDeleteCompilation() throws Exception {
        mockMvc.perform(delete("/admin/compilations/5")).andExpect(status().isNoContent());

        verify(service).adminRemoveCompilation(5);
    }

    @Test
    void shouldHandleNotFoundOnDelete() throws Exception {
        doThrow(new CompilationNotFound(5L)).when(service).adminRemoveCompilation(5L);

        mockMvc.perform(delete("/admin/compilations/5")).andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateCompilation() throws Exception {
        UpdateCompilationDto dto = new UpdateCompilationDto();
        dto.setTitle("Updated");

        CompilationDto result = new CompilationDto();
        result.setId(10L);

        when(service.adminUpdateCompilation(eq(10L), any())).thenReturn(result);

        mockMvc.perform(patch("/admin/compilations/10").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));

        verify(service).adminUpdateCompilation(eq(10L), any());
    }
}
