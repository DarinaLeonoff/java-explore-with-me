package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private int id;
    @Size(min = 1, max = 50, message = "Длина названия должна быль от 1 до 50 символов")
    private String name;
}
