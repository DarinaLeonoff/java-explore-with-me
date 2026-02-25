package ru.practicum.ewm.category.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.notFound.CategoryNotFound;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryPublicServiceTest {
    @Mock
    private CategoryRepository repository;
    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryServiceImpl service;

    @Test
    void getAllCategoriesByPagesTest() {
        List<Category> list = List.of(Category.builder().id(1).name("name 1").build(),
                Category.builder().id(2).name("name 2").build(), Category.builder().id(3).name("name 3").build());

        Pageable pageable = PageRequest.of(0 / 10, 10, Sort.by("id").ascending());

        when(repository.findCategories(pageable)).thenReturn(list);

        when(mapper.mapCategoryToDto(list.get(0))).thenReturn(CategoryDto.builder().id(1).name("name 1").build());
        when(mapper.mapCategoryToDto(list.get(1))).thenReturn(CategoryDto.builder().id(2).name("name 2").build());
        when(mapper.mapCategoryToDto(list.get(2))).thenReturn(CategoryDto.builder().id(3).name("name 3").build());

        List<CategoryDto> res = service.getPublicCategories(0, 10);
        CategoryDto first = res.getFirst();
        assertEquals(3, res.size());
        assertEquals(1, first.getId());
        assertEquals("name 1", first.getName());
    }

    @Test
    void getCategoryByIdTest() {
        Category cat = Category.builder().id(1).name("name 1").build();
        CategoryDto dto = CategoryDto.builder().id(1).name("name 1").build();

        when(repository.findById(1)).thenReturn(Optional.ofNullable(cat));

        when(mapper.mapCategoryToDto(cat)).thenReturn(dto);

        CategoryDto res = service.getPublicCategory(1);

        assertEquals(1, res.getId());
        assertEquals("name 1", res.getName());
    }

    @Test
    void getCategoryByIdFallTest() {

        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFound.class, () -> service.getPublicCategory(1));
    }
}
