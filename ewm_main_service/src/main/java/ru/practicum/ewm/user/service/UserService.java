package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    //public
    UserDto publicGetUserById(long id);

    // admin
    UserDto adminCreateUser(NewUserRequestDto request);

    List<UserDto> adminGetUsers(List<Long> ids, int from, int size);

    void adminDeleteUser(long userId);
}
