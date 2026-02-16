package ru.practicum.ewm.compilation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationPublicService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompilationPublicController.class)
public class CompilationPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationPublicService service;

    @Autowired
    private ObjectMapper mapper;

    CompilationDto dto;

    @BeforeEach
    void setup() {
        dto = CompilationDto.builder().id(1L).title("Title").pinned(false).events(List.of()).build();
    }

    @Test
    void shouldReturnCompilationList() throws Exception {
        when(service.getCompilations(anyBoolean(), anyInt(), anyInt())).thenReturn(List.of(dto));

        mockMvc.perform(get("/compilations?pinned=false&from=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(List.of(dto)))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldPassNullPinnedToService() throws Exception {

        when(service.getCompilations(null, 0, 10))
                .thenReturn(List.of());

        mockMvc.perform(get("/compilations"))
                .andExpect(status().isOk());

        verify(service).getCompilations(null, 0, 10);
    }

    @Test
    void shouldReturnCompilation() throws Exception {

        when(service.getCompilationById(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/compilations/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.title").value(dto.getTitle()));

        verify(service).getCompilationById(eq(1L));
    }
}
