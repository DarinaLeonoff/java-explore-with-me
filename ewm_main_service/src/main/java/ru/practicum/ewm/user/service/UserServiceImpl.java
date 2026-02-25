package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto publicGetUserById(long id) {
        return mapper.mapUserToUserDto(getUser(id));
    }

    @Override
    public UserDto adminCreateUser(NewUserRequestDto request) {
        UserDto res = mapper.mapUserToUserDto(repository.save(mapper.mapNewUserToUser(request)));
        log.info("Saved new user with id = {}", res.getId());
        return res;
    }

    @Override
    public List<UserDto> adminGetUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<User> users = repository.findUsers(ids, pageable);
        return users.stream().map(mapper::mapUserToUserDto).toList();
    }

    @Override
    public void adminDeleteUser(long userId) {
        log.info("Admin deleting user({})", userId);
        User user = getUser(userId);
        repository.delete(user);
    }

    private User getUser(Long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFound(id));
    }
}
