package ru.practicum.ewm.exception.notFound;

public class CompilationNotFound extends NotFoundException {
    public CompilationNotFound(long id) {
        super("Compilation with id=\" + id + \" was not found");
    }
}
