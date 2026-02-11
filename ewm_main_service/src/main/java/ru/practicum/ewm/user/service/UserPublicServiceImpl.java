package ru.practicum.ewm.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.notFound.UserNotFound;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;

@Service
public class UserPublicServiceImpl implements UserPublicService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserMapper mapper;


    @Override
    public UserDto getUserById(long id) {
        return mapper.mapUserToUserDto(repository.findById(id).orElseThrow(() -> new UserNotFound(id)));
    }
}
