package ru.practicum.ewm.validationAnnotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.ewm.request.model.RequestState;

import java.util.Set;

public class AllowedRequestStatusValidator
        implements ConstraintValidator<ConfirmOrRejectedRequest, RequestState> {
    private static final Set<RequestState> ALLOWED =
            Set.of(RequestState.CONFIRMED, RequestState.REJECTED);

    @Override
    public boolean isValid(RequestState value,
            ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        return ALLOWED.contains(value);
    }
}
