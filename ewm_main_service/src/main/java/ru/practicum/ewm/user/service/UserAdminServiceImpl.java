package ru.practicum.ewm.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.notFound.UserNotFound;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class UserAdminServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDto createUser(NewUserRequestDto request) {
        UserDto res = userMapper.mapUserToUserDto(userRepository.save(userMapper.mapNewUserToUser(request)));
        log.info("Saved new user with id = {}", res.getId());
        return res;
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<User> users = userRepository.findUsers(ids, pageable);
        return users.stream().map(userMapper::mapUserToUserDto).toList();
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Admin deleting user({})", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));
        userRepository.delete(user);
    }
}
