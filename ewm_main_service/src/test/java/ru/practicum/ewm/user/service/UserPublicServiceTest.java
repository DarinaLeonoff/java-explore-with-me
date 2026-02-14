package ru.practicum.ewm.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.exception.notFound.UserNotFound;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPublicServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserPublicServiceImpl userService;

    private User user;
    private UserDto dto;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("John Doe").email("john@ya.ru").build();
        dto = new UserDto(1L, "John Doe", "john@ya.ru");
    }

    @Test
    void getUserTest() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        when(userMapper.mapUserToUserDto(user)).thenReturn(dto);

        UserDto result = userService.getUserById(user.getId());

        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getEmail(), result.getEmail());

        verify(userRepository).findById(user.getId());
        verify(userMapper).mapUserToUserDto(user);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class,
                () -> userService.getUserById(id));

        verify(userRepository).findById(id);
    }
}
