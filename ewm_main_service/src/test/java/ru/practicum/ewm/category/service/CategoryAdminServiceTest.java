package ru.practicum.ewm.category.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryAdminServiceTest {
    @Mock
    private CategoryRepository repository;
    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryAdminServiceImpl service;

    @Test
    void createCategoryTest() {
        NewCategoryDto newCat = new NewCategoryDto();
        newCat.setName("new cat");

        Category category = Category.builder().id(1).name(newCat.getName()).build();

        CategoryDto categoryDto = CategoryDto.builder().id(1).name(newCat.getName()).build();

        when(mapper.mapNewCategoryDtoToCategory(newCat)).thenReturn(category);

        when(repository.save(category)).thenReturn(category);

        when(mapper.mapCategoryToDto(category)).thenReturn(categoryDto);

        CategoryDto dto = service.createCategory(newCat);

        assertEquals("new cat", dto.getName());
    }

    @Test
    void updateCategoryTest() {
        CategoryDto dto = CategoryDto.builder().name("update Cat").build();
        Category category = Category.builder().id(1).name("cat").build();
        category.setName(dto.getName());
        when(repository.findById(1)).thenReturn(Optional.ofNullable(category));

        category.setName(dto.getName());
        when(mapper.updateCategory(dto, category)).thenReturn(category);
        when(repository.save(category)).thenReturn(category);
        dto.setId(category.getId());
        when(mapper.mapCategoryToDto(category)).thenReturn(dto);

        CategoryDto updated = service.aditCategory(dto, 1);

        assertEquals("update Cat", updated.getName());
        assertEquals(1, updated.getId());
    }

    @Test
    void deleteCategoryTest() {
        Category category = Category.builder().id(1).name("cat").build();
        when(repository.findById(1)).thenReturn(Optional.ofNullable(category));
        service.removeCategory(1);
        verify(repository, times(1)).deleteById(anyInt());
    }
}
