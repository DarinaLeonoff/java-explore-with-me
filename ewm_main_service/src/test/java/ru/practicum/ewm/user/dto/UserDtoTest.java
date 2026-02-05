package ru.practicum.ewm.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@ContextConfiguration(classes = UserDto.class)
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void shouldSerializeUserDto() throws Exception {
        // given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        // when
        var jsonContent = jacksonTester.write(userDto);

        // then
        assertThat(jsonContent).hasJsonPathNumberValue("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email")
                .isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldDeserializeUserDto() throws Exception {
        // given
        String json = "{\n" +
                "  \"id\": \"2\",\n" +
                "  \"name\": \"Jane Doe\",\n" +
                "  \"email\": \"jane.doe@example.com\"\n" +
                "}";

        // when
        UserDto userDto = jacksonTester.parseObject(json);

        // then
        assertThat(userDto.getId()).isEqualTo(2L);
        assertThat(userDto.getName()).isEqualTo("Jane Doe");
        assertThat(userDto.getEmail()).isEqualTo("jane.doe@example.com");
    }
}
