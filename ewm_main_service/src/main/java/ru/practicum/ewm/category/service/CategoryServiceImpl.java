package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto adminCreateCategory(NewCategoryDto dto) {
        Category setCat = mapper.mapNewCategoryDtoToCategory(dto);
        Category cat = repository.save(setCat);
        return mapper.mapCategoryToDto(cat);
    }

    @Override
    public CategoryDto adminUpdateCategory(CategoryDto dto, int id) {
        Category cat = getCategory(id);
        Category edited = repository.save(updateCategory(dto, cat));
        return mapper.mapCategoryToDto(edited);
    }

    @Override
    public void adminRemoveCategory(int id) {
        Category cat = getCategory(id);
        repository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getPublicCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return repository.findCategories(pageable).stream().map(mapper::mapCategoryToDto).toList();
    }

    @Override
    public CategoryDto getPublicCategory(int catId) {
        return mapper.mapCategoryToDto(getCategory(catId));
    }

    private Category getCategory(Integer id) {
        return getCategory(id);
    }

    private Category updateCategory(CategoryDto changes, Category category) {
        if (changes.getName() != null) {
            category.setName(changes.getName());
        }
        return category;
    }
}
