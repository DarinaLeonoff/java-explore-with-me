package ru.practicum.ewm.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.notFound.CategoryNotFound;

import java.util.List;

@Service
public class CategoryPublicServiceImpl implements CategoryPublicService {
    @Autowired
    private CategoryRepository repository;
    @Autowired
    private CategoryMapper mapper;

    //todo
    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return repository.findCategories(pageable).stream().map(mapper::mapCategoryToDto).toList();
    }

    //todo
    @Override
    public CategoryDto getCategory(int catId) {
        return mapper.mapCategoryToDto(repository.findById(catId).orElseThrow(() -> new CategoryNotFound(catId)));
    }
}
