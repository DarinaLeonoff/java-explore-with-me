package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserDto;

public interface UserPublicService {
    UserDto getUserById(long id);
}
