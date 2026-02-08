package ru.practicum.ewm.category.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Valid
public class NewCategoryDto {
    @NotNull(message = "Должно быть указано название категории")
    @Size(min = 1, max = 50, message = "Длина названия должна быль от 1 до 50 символов")
    private String name;
}
