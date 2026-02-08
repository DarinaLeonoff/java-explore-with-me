package ru.practicum.ewm.category.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryAdminService;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    @Autowired
    private CategoryAdminService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto createNewCategory(@Valid @RequestBody NewCategoryDto dto) {
        log.info("Admin creating new category");
        return service.createCategory(dto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    CategoryDto aditCategory(@PathVariable int catId,
            @Valid @RequestBody CategoryDto dto) {
        log.info("Admin make changes in category {}", catId);
        return service.aditCategory(dto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeCategory(@PathVariable int catId) {
        log.info("Admin remove category {}", catId);
        service.removeCategory(catId);
    }
}
