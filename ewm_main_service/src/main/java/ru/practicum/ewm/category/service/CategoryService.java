package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto adminCreateCategory(NewCategoryDto dto);

    CategoryDto adminUpdateCategory(CategoryDto dto, int id);

    void adminRemoveCategory(int id);

    List<CategoryDto> getPublicCategories(int from, int size);

    CategoryDto getPublicCategory(int catId);
}
