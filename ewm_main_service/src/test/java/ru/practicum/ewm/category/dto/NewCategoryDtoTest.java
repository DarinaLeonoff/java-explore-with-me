package ru.practicum.ewm.category.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@ContextConfiguration(classes = NewCategoryDto.class)
@Import(LocalValidatorFactoryBean.class)
public class NewCategoryDtoTest {
    @Autowired
    private JacksonTester<NewCategoryDto> jacksonTester;
    @Autowired
    private Validator validator;

    @Test
    void shouldSerializeNewCategoryDto() throws Exception {
        NewCategoryDto dto = new NewCategoryDto();
        dto.setName("new category");

        JsonContent<NewCategoryDto> jsonContent = jacksonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
    }

    @Test
    void shouldDeserializeNewUserDto() throws Exception {
        String json = "{\n" + "  \"name\": \"new category name\"\n" + "}";

        NewCategoryDto dto = jacksonTester.parseObject(json);

        AssertionsForClassTypes.assertThat(dto.getName()).isEqualTo("new category name");
    }

    @ParameterizedTest
    @NullSource
    void shouldThrowExceptionWithNameNull(String name) {
        NewCategoryDto dto = new NewCategoryDto();
        dto.setName(name);

        Set<ConstraintViolation<NewCategoryDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        ConstraintViolation<NewCategoryDto> violation = violations.iterator().next();

        AssertionsForClassTypes.assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        AssertionsForClassTypes.assertThat(violation.getMessage()).isEqualTo("Должно быть указано название категории");
    }

    @ParameterizedTest
    @EmptySource
    void shouldThrowExceptionWithNameEmptyString(String name) {
        NewCategoryDto dto = new NewCategoryDto();
        dto.setName(name);

        Set<ConstraintViolation<NewCategoryDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        ConstraintViolation<NewCategoryDto> violation = violations.iterator().next();

        AssertionsForClassTypes.assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        AssertionsForClassTypes.assertThat(violation.getMessage()).isEqualTo("Длина названия должна быль от 1 до 50 символов");
    }
}
