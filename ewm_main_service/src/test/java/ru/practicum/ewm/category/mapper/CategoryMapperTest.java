package ru.practicum.ewm.category.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CategoryMapperTest {
    private CategoryMapper mapper = new CategoryMapperImpl();

    @Test
    void convertNewCategoryDtoToCategoryTest() {
        NewCategoryDto dto = new NewCategoryDto();
        dto.setName("new category");

        Category result = mapper.mapNewCategoryDtoToCategory(dto);

        assertEquals(dto.getName(), result.getName());
        assertNull(result.getId());
    }

    @Test
    void convertCategoryToDtoTest() {
        Category category = Category.builder().id(1).name("new cat").build();

        CategoryDto result = mapper.mapCategoryToDto(category);

        assertEquals(category.getId(), result.getId());
        assertEquals(category.getName(), result.getName());
    }

    @Test
    void updateCategoryTest() {
        Category cat = Category.builder().id(1).name("cat").build();
        CategoryDto change = CategoryDto.builder().name("cat2").build();

        Category result = mapper.updateCategory(change, cat);

        assertEquals(change.getName(), result.getName());
        assertEquals(cat.getId(), result.getId());
    }

    @Test
    void updateCategoryWithNullNameTest() {
        Category cat = Category.builder().id(1).name("cat").build();
        CategoryDto change = CategoryDto.builder().build();

        Category result = mapper.updateCategory(change, cat);

        assertEquals(cat.getName(), result.getName());
        assertEquals(cat.getId(), result.getId());
    }

}
