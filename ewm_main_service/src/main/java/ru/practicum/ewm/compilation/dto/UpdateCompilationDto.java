package ru.practicum.ewm.compilation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Valid
public class UpdateCompilationDto {
    @Size(min = 1, max = 50, message = "title должен содержать от 1 до 50 символов.")
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
