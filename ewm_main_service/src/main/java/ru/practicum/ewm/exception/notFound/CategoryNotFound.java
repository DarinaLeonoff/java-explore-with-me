package ru.practicum.ewm.exception.notFound;

public class CategoryNotFound extends NotFoundException {
    public CategoryNotFound(int id) {
        super("Category with id=" + id + " was not found");
    }
}
