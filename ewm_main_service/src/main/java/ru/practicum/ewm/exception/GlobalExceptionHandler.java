package ru.practicum.ewm.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.notFound.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleWrongRequest(final MethodArgumentNotValidException e) {
        String[] errors = e.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).toArray(String[]::new);
        FieldError error = e.getBindingResult().getFieldErrors().get(0);

        String message = String.format("Field: %s. Error: %s. Value: %s", error.getField(), error.getDefaultMessage(), error.getRejectedValue());

        return new ErrorResponse(errors, HttpStatus.BAD_REQUEST.name(), "Incorrectly made request.", message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(DataIntegrityViolationException e) {

        return new ErrorResponse(null, HttpStatus.CONFLICT.name(), "Integrity constraint has been violated.", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        return new ErrorResponse(null, HttpStatus.NOT_FOUND.name(), "The required object was not found.", e.getMessage());
    }

}
