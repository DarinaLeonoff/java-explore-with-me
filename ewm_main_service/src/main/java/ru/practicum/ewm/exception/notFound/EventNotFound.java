package ru.practicum.ewm.exception.notFound;

public class EventNotFound extends NotFoundException {
    public EventNotFound(long id) {
        super("Event with id=" + id + " was not found");
    }
}
