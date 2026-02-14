package ru.practicum.ewm.event.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.event.dto.updateDto.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserUpdatesState {
    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenValidRequest() {
        UpdateEventUserRequest request =
                UpdateEventUserRequest.builder().stateAction(StateUserAction.CANCEL_REVIEW).build();

        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassValidation_whenSendRequest() {
        UpdateEventUserRequest request =
                UpdateEventUserRequest.builder().stateAction(StateUserAction.SEND_TO_REVIEW).build();

        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}
