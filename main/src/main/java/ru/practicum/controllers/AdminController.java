package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.entities.Category;
import ru.practicum.services.CategoriesService;
import static ru.practicum.mappers.CategoryMapper.toEntity;
import static ru.practicum.mappers.CategoryMapper.toDto;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CategoriesService categoriesService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> categories(@Valid @RequestBody CategoryDto categoryDto) {
        Category category = toEntity(categoryDto);
        category = categoriesService.saveCategory(category);
        return new ResponseEntity<>(toDto(category), HttpStatus.OK);
    }
}
