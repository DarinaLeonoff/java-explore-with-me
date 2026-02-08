package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

public interface CategoryAdminService {
    CategoryDto createCategory(NewCategoryDto dto);

    CategoryDto aditCategory(CategoryDto dto, int id);

    void removeCategory(int id);
}
