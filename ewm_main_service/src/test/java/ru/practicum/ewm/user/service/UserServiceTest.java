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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private NewUserRequestDto req;
    private User user;
    private UserDto dto;

    @BeforeEach
    void setup() {
        req = NewUserRequestDto.builder().name("new user name").email("newUser@ya.ru").build();
        user = User.builder().id(1L).name("John Doe").email("john@ya.ru").build();
        dto = new UserDto(1L, "John Doe", "john@ya.ru");
    }

    @Test
    void createUserTest() {
        User user = new User();
        user.setId(1L);
        user.setName(req.getName());
        user.setEmail(req.getEmail());

        UserDto dto = new UserDto(1L, req.getName(), req.getEmail());

        when(userMapper.mapNewUserToUser(req)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapUserToUserDto(user)).thenReturn(dto);

        UserDto result = userService.adminCreateUser(req);

        assertEquals(1L, result.getId());
        assertEquals(req.getName(), result.getName());
        assertEquals(req.getEmail(), result.getEmail());
    }

    @Test
    void getUserListTest() {
        List<Long> ids = List.of(1L);
        int from = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        when(userRepository.findUsers(ids, pageable)).thenReturn(List.of(user));

        when(userMapper.mapUserToUserDto(user)).thenReturn(dto);

        List<UserDto> result = userService.adminGetUsers(ids, from, size);

        assertEquals(1, result.size());
        UserDto resultDto = result.getFirst();

        assertEquals(dto.getId(), resultDto.getId());
        assertEquals(dto.getName(), resultDto.getName());
        assertEquals(dto.getEmail(), resultDto.getEmail());

        verify(userRepository).findUsers(ids, pageable);
        verify(userMapper).mapUserToUserDto(user);
    }

    @Test
    void adminDeleteUserTest() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.adminDeleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void adminDeleteUserFallTest() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userService.adminDeleteUser(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void getUserTest() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        when(userMapper.mapUserToUserDto(user)).thenReturn(dto);

        UserDto result = userService.publicGetUserById(user.getId());

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

        assertThrows(UserNotFound.class, () -> userService.publicGetUserById(id));

        verify(userRepository).findById(id);
    }
}
