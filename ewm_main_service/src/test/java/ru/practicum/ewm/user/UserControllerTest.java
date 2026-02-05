package ru.practicum.ewm.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getShouldReturnOk() throws Exception {
        UserDto dto1 = UserDto.builder().id(1L).name("name").email("emain@ya.ru").build();
        UserDto dto2 = UserDto.builder().id(2L).name("name").email("emain@ya.ru").build();

        mockMvc.perform(get("/admin/users?ids=1&ids=2").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(List.of(dto1, dto2)))).andExpect(status().isOk());


    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/admin/users/1")).andExpect(status().isNoContent());

        verify(service, times(1)).deleteUser(1);
    }

    @Test
    void postShouldReturnCreated() throws Exception {
        NewUserRequestDto dto = NewUserRequestDto.builder().name("nnnnName").email("email@ya.ru").build();
        UserDto userDto = UserDto.builder().id(1L).name("nnnnName").email("email@ya.ru").build();

        when(service.createUser(any(NewUserRequestDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/admin/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated()).andExpect(jsonPath("$.name").value(userDto.getName())).andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }
}
