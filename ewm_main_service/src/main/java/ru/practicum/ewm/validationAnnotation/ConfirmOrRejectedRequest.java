package ru.practicum.ewm.validationAnnotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedRequestStatusValidator.class)
@Documented
public @interface ConfirmOrRejectedRequest {
    String message() default "Недопустимое значение статуса";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    }

