package ru.practicum.ewm.exception.notFound;

public class CommentNotFound extends NotFoundException {
    public CommentNotFound(long id) {
        super("Compilation with id=" + id + " was not found");
    }
}
