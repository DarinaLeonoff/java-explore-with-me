package ru.practicum.ewm.exception;

public class UserNotFound extends NotFoundException {
    public UserNotFound(long id) {
        super("User with id=" + id + " was not found");
    }
}
