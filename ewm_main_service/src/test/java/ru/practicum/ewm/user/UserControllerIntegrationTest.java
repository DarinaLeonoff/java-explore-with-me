package ru.practicum.ewm.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    UserDto user;

    @BeforeEach
    void setup() throws Exception {
        NewUserRequestDto dto = NewUserRequestDto.builder().name("TestUser").email("test@mail.com").build();

        String userResponse = mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andReturn().getResponse().getContentAsString();

        user = objectMapper.readValue(userResponse, UserDto.class);
    }


    @Test
    void shouldCreateUser() throws Exception {
        assertEquals("TestUser", user.getName());
        assertEquals("test@mail.com", user.getEmail());
    }

    @Test
    void shouldReturnUsers() throws Exception {
        mockMvc.perform(get("/admin/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(hasItem(user.getId().intValue())));
    }

    @Test
    void shouldReturnUsersByIds() throws Exception {
        mockMvc.perform(get("/admin/users?ids=" + user.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/admin/users/" + user.getId())).andExpect(status().isNoContent());

        mockMvc.perform(get("/admin/users?ids=" + user.getId())).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidEmail() throws Exception {
        NewUserRequestDto dto = NewUserRequestDto.builder().name("Test").email("wrongEmail").build();

        mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
    }
}
