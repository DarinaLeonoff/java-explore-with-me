package ru.practicum.ewm.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;

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


    @Test
    void shouldCreateUser() throws Exception {
        NewUserRequestDto dto = NewUserRequestDto.builder().name("TestUser").email("test@mail.com").build();

        mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.name").value("TestUser"));
    }

    @Test
    void shouldReturnUsers() throws Exception {
        NewUserRequestDto dto = NewUserRequestDto.builder().name("User1").email("user1@mail.com").build();

        mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/admin/users")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnUsersByIds() throws Exception {
        String response = mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewUserRequestDto("User", "user@mail.com")))).andReturn()
                .getResponse().getContentAsString();

        UserDto created = objectMapper.readValue(response, UserDto.class);

        mockMvc.perform(get("/admin/users?ids=" + created.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(created.getId()));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        String response = mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewUserRequestDto("User", "user@mail.com")))).andReturn()
                .getResponse().getContentAsString();

        UserDto created = objectMapper.readValue(response, UserDto.class);

        mockMvc.perform(delete("/admin/users/" + created.getId())).andExpect(status().isNoContent());

        mockMvc.perform(get("/admin/users?ids=" + created.getId())).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidEmail() throws Exception {
        NewUserRequestDto dto = NewUserRequestDto.builder().name("Test").email("wrongEmail").build();

        mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
    }
}
