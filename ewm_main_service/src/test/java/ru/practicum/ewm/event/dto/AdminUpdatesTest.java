package ru.practicum.ewm.event.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.event.dto.updateDto.StateAdminAction;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminUpdatesTest {
    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenPublishRequest() {
        UpdateEventAdminRequest request = UpdateEventAdminRequest.builder().stateAction(StateAdminAction.PUBLISH_EVENT).build();

        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassValidation_whenRejectRequest() {
        UpdateEventAdminRequest request = UpdateEventAdminRequest.builder().stateAction(StateAdminAction.REJECT_EVENT).build();

        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}
