package ru.practicum.ewm.exception.notFound;

public class RequestNotFound extends NotFoundException {
    public RequestNotFound(long id) {
        super("Request with id=" + id + " was not found");
    }
}
