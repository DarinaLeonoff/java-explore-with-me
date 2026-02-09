package ru.practicum.ewm.user.dto;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@ContextConfiguration(classes = UserShortDto.class)
public class UserShortDtoTest {
    @Autowired
    private JacksonTester<UserShortDto> jacksonTester;

    @Test
    void shouldSerializeUserDto() throws Exception {
        UserShortDto userDto = UserShortDto.builder()
                .id(1L)
                .name("John Doe")
                .build();

        var jsonContent = jacksonTester.write(userDto);

        assertThat(jsonContent).hasJsonPathNumberValue("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
    }

    @Test
    void shouldDeserializeUserDto() throws Exception {
        String json = "{\n" +
                "  \"id\": \"2\",\n" +
                "  \"name\": \"Jane Doe\"\n" +
                "}";

        UserShortDto userDto = jacksonTester.parseObject(json);

        AssertionsForClassTypes.assertThat(userDto.getId()).isEqualTo(2L);
        AssertionsForClassTypes.assertThat(userDto.getName()).isEqualTo("Jane Doe");
    }
}
