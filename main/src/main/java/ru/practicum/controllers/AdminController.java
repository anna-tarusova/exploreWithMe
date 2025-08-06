package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.UserDto;
import ru.practicum.entities.Category;
import ru.practicum.entities.User;
import ru.practicum.services.CategoriesService;
import ru.practicum.services.UserService;


import java.util.List;

import static ru.practicum.mappers.CategoryMapper.toEntity;
import static ru.practicum.mappers.CategoryMapper.toDto;
import static ru.practicum.mappers.UserMapper.toEntity;
import static ru.practicum.mappers.UserMapper.toDto;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CategoriesService categoriesService;
    private final UserService userService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Category category = toEntity(categoryDto);
        category = categoriesService.saveCategory(category);
        return new ResponseEntity<>(toDto(category), HttpStatus.OK);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable("catId") Long catId) {
        categoriesService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> partiallyUpdateCategory(@PathVariable("catId") Long catId, @RequestBody CategoryDto categoryDto) {
        Category category = toEntity(categoryDto);
        category = categoriesService.partiallyUpdate(category, catId);
        return new ResponseEntity<>(toDto(category), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        User user = toEntity(userDto);
        user = userService.saveUser(user);
        return new ResponseEntity<>(toDto(user), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return userService.getUsers(ids, from, size);
    }
}
