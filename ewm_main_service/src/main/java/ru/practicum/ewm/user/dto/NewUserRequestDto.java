package ru.practicum.ewm.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Valid
public class NewUserRequestDto {
    @NotBlank
    @Size(min = 2, max = 250, message = "Имя должно содержать от 2 до 250 символов")
    private String name;

    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "Email должен содержать от 6 до 254 символов")
    private String email;
}
