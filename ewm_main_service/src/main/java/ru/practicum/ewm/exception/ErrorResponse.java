package ru.practicum.ewm.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrorResponse {
    private final String[] errors;
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ErrorResponse(String[] errors, String status, String reason, String message) {

        this.errors = errors;
        this.status = status;
        this.reason = reason;
        this.message = message;
        timestamp = LocalDateTime.now().format(FORMATTER);
    }
}
