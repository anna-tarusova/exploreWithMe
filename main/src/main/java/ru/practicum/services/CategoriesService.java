package ru.practicum.services;

import ru.practicum.entities.Category;

public interface CategoriesService {
    Category saveCategory(Category category);
    void deleteCategory(Long id);
    Category partiallyUpdate(Category category, Long id);
}
