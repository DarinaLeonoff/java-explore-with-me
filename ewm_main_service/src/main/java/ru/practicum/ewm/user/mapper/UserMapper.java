package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto mapUserToUserDto(User user);

    User mapNewUserToUser(NewUserRequestDto dto);
}
