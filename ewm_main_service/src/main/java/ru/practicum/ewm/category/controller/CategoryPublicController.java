package ru.practicum.ewm.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryPublicService;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryPublicController {
    @Autowired
    private CategoryPublicService service;

    @GetMapping
    List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto getCategory(@PathVariable int catId) {
        return service.getCategory(catId);
    }
}
