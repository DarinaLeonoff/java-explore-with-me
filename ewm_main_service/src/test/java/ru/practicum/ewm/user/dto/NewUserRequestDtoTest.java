package ru.practicum.ewm.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@Import(LocalValidatorFactoryBean.class)
public class NewUserRequestDtoTest {
    @Autowired
    private JacksonTester<NewUserRequestDto> jacksonTester;

    @Autowired
    private Validator validator;

    @Test
    void shouldSerializeNewUserRequestDto() throws Exception {

        NewUserRequestDto dto = NewUserRequestDto.builder().name("John Doe").email("john.doe@example.com").build();

        var json = jacksonTester.write(dto);

        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldDeserializeNewUserRequestDto() throws Exception {
        String json = "{\n" + "  \"name\": \"John Doe\",\n" + "  \"email\": \"john.doe@example.com\"\n" + "}";

        NewUserRequestDto dto = jacksonTester.parseObject(json);

        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
    }

//    @Test
//    void shouldFailValidationWhenNameIsTooShort() {
//        // given
//        NewUserRequestDto dto = NewUserRequestDto.builder()
//                .name("John")
//                .email("john.doe@example.com")
//                .build();
//
//        // when
//        Set<ConstraintViolation<NewUserRequestDto>> violations =
//                validator.validate(dto);
//
//        // then
//        assertThat(violations).hasSize(1);
//        ConstraintViolation<NewUserRequestDto> violation = violations.iterator().next();
//
//        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
//        assertThat(violation.getMessage())
//                .isEqualTo("Имя должно содержать от 6 до 254 символов");
//    }

    @Test
    void shouldFailValidationWhenEmailIsInvalid() {
        // given
        NewUserRequestDto dto = NewUserRequestDto.builder().name("John Doe").email("not-an-email").build();

        // when
        Set<ConstraintViolation<NewUserRequestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void shouldFailValidationWhenFieldsAreNull() {
        // given
        NewUserRequestDto dto = new NewUserRequestDto();

        // when
        Set<ConstraintViolation<NewUserRequestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsExactlyInAnyOrder("name", "email");
    }
}
