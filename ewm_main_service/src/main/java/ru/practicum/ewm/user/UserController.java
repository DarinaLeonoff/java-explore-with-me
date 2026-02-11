package ru.practicum.ewm.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserAdminService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
public class UserController {
    @Autowired
    private UserAdminService userService;

    //    получение информации о пользователях (List<UserDto>)
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> adminGetUsers(@RequestParam(required = false) List<Long> ids, @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Admin getting users from = {}, size = " + "{}, ids: {}", from, size, ids);
        return userService.getUsers(ids, from, size);
    }

    //    добавление нового пользователя
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto adminPostUser(@Valid @RequestBody NewUserRequestDto request) {
        log.info("Admin adding new User");
        return userService.createUser(request);
    }

    //    Удаление пользователей
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteUser(@PathVariable long userId) {
        log.info("Admin deleting user({})", userId);
        userService.deleteUser(userId);
    }

}
