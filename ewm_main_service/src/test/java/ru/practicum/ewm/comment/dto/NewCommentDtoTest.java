package ru.practicum.ewm.comment.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
@ContextConfiguration(classes = NewCommentDto.class)
@Import(LocalValidatorFactoryBean.class)
public class NewCommentDtoTest {
    @Autowired
    private JacksonTester<NewCommentDto> jacksonTester;
    @Autowired
    private Validator validator;

    @Test
    void shouldSerializeCategoryDto() throws Exception {
        NewCommentDto dto = NewCommentDto.builder().text("new Comment").build();

        JsonContent<NewCommentDto> jsonContent = jacksonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
    }

    @Test
    void shouldDeserializeCategoryDto() throws Exception {
        String json = "{\n" + "  \"text\": \"new Comment\"\n" + "}";

        NewCommentDto dto = jacksonTester.parseObject(json);

        AssertionsForClassTypes.assertThat(dto.getText()).isEqualTo("new Comment");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "\t", "\n"})
    void exceptionIfNameShort(String text) {
        NewCommentDto dto = NewCommentDto.builder().text(text).build();

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        ConstraintViolation<NewCommentDto> violation = violations.iterator().next();

        AssertionsForClassTypes.assertThat(violation.getPropertyPath().toString()).isEqualTo("text");
    }
}
