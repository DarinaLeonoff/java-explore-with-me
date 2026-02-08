package ru.practicum.ewm.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.notFound.CategoryNotFound;

@Slf4j
@Service
public class CategoryAdminServiceImpl implements CategoryAdminService {
    @Autowired
    private CategoryRepository repository;
    @Autowired
    private CategoryMapper mapper;

    @Override
    public CategoryDto createCategory(NewCategoryDto dto) {
        Category setCat = mapper.mapNewCategoryDtoToCategory(dto);
        Category cat = repository.save(setCat);
        return mapper.mapCategoryToDto(cat);
    }

    @Override
    public CategoryDto aditCategory(CategoryDto dto, int id) {
        Category cat = repository.findById(id).orElseThrow(() -> new CategoryNotFound(id));
        Category edited = repository.save(mapper.updateCategory(dto, cat));
        return mapper.mapCategoryToDto(edited);
    }

    @Override
    public void removeCategory(int id) {
        Category cat = repository.findById(id).orElseThrow(() -> new CategoryNotFound(id));
        repository.deleteById(id);
    }
}
