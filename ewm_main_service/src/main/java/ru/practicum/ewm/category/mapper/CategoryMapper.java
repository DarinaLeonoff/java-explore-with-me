package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category mapNewCategoryDtoToCategory(NewCategoryDto dto);

    CategoryDto mapCategoryToDto(Category cat);

    default Category updateCategory(CategoryDto changes, Category category) {
        if (changes.getName() != null) {
            category.setName(changes.getName());
        }
        return category;
    }
}
