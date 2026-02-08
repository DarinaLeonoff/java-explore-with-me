package ru.practicum.ewm.exception.notFound;

public class UserNotFound extends NotFoundException {
    public UserNotFound(long id) {
        super("User with id=" + id + " was not found");
    }
}
