package ru.practicum.ewm.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {

    private UserMapper userMapper = new UserMapperImpl();

    private User user;
    private NewUserRequestDto userRequest;
    private UserDto dto;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("NewName").email("email@ya.ru").build();
        userRequest = NewUserRequestDto.builder().name("NewName").email("email@ya.ru").build();
        dto = UserDto.builder().id(1L).name("NewName").email("email@ya.ru").build();
    }

    @Test
    void convertUserToDto() {
        UserDto result = userMapper.mapUserToUserDto(user);

        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void convertUserRequestToUser() {
        User result = userMapper.mapNewUserToUser(userRequest);

        assertNull(result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

}
