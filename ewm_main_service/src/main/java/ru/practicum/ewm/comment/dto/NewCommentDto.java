package ru.practicum.ewm.comment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Valid
public class NewCommentDto {
    @NotBlank
    @Size(min = 1, max = 3000, message = "Комментарий должен содержать от 1 до 3000 символов.")
    private String text;
}
