package ru.practicum.ewm.category.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
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
@ContextConfiguration(classes = CategoryDto.class)
@Import(LocalValidatorFactoryBean.class)
public class CategoryDtoTest {
    @Autowired
    private JacksonTester<CategoryDto> jacksonTester;
    @Autowired
    private Validator validator;


    @Test
    void shouldSerializeCategoryDto() throws Exception {
        CategoryDto dto = CategoryDto.builder().id(1).name("category").build();

        JsonContent<CategoryDto> jsonContent = jacksonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
    }

    @Test
    void shouldDeserializeUserDto() throws Exception {
        String json = "{\n" + "  \"id\": \"2\",\n" + "  \"name\": \"category name\"\n" + "}";

        CategoryDto dto = jacksonTester.parseObject(json);

        AssertionsForClassTypes.assertThat(dto.getId()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(dto.getName()).isEqualTo("category name");
    }

    @ParameterizedTest
    @EmptySource
    void exceptionIfNameShort(String name) {
        CategoryDto dto = CategoryDto.builder().name(name).build();

        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        ConstraintViolation<CategoryDto> violation = violations.iterator().next();

        AssertionsForClassTypes.assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        AssertionsForClassTypes.assertThat(violation.getMessage()).isEqualTo("Длина названия должна быль от 1 до 50 символов");
    }
}
