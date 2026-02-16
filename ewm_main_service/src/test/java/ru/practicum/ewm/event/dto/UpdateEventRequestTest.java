package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventRequest;
import ru.practicum.ewm.event.model.Location;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateEventRequestTest {
    private static Validator validator;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UpdateEventRequest createValidRequest() {
        UpdateEventRequest request = new UpdateEventRequest();
        request.setAnnotation("This is a valid annotation with more than 20 characters");
        request.setDescription("This is a valid description with more than 20 characters");
        request.setTitle("Valid title");
        request.setEventDate(LocalDateTime.now());
        request.setPaid(true);
        request.setParticipantLimit(100);
        request.setRequestModeration(false);
        request.setCategory(1);
        request.setLocation(new Location());
        return request;
    }

//    @Test
//    void shouldPassValidation_whenValidRequest() {
//        UpdateEventRequest request = createValidRequest();
//
//        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);
//
//        assertTrue(violations.isEmpty());
//    }

    @Test
    void shouldFail_whenAnnotationTooShort() {
        UpdateEventRequest request = createValidRequest();
        request.setAnnotation("short");

        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFail_whenDescriptionTooShort() {
        UpdateEventRequest request = createValidRequest();
        request.setDescription("short");

        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFail_whenTitleTooShort() {
        UpdateEventRequest request = createValidRequest();
        request.setTitle("ab");

        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAllowNullFields_whenUpdatingPartial() {
        UpdateEventRequest request = new UpdateEventRequest();

        // DTO для обновления → допускаем null
        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFail_whenAnnotationTooLong() {
        UpdateEventRequest request = createValidRequest();
        request.setAnnotation("a".repeat(2001));

        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFail_whenDescriptionTooLong() {
        UpdateEventRequest request = createValidRequest();
        request.setDescription("a".repeat(7001));

        Set<ConstraintViolation<UpdateEventRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldDeserializeEventDate() throws Exception {
        String json = "{\n" +
                  "\"eventDate\":\"2025-12-01 10:15:30\"\n" +
                "}";

        UpdateEventRequest request = mapper.readValue(json, UpdateEventRequest.class);

        assertNotNull(request.getEventDate());
    }
}
