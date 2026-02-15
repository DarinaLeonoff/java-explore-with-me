package ru.practicum.ewm.compilation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Valid
public class NewCompilationDto {
    @NotNull
    @Size(min = 1, max = 50, message = "title должен содержать от 1 до 50 символов.")
    private String title;
    @Builder.Default
    private boolean pinned = false;
    @Builder.Default
    private List<Long> events = new ArrayList<>();
}
