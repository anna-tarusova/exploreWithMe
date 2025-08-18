package ru.practicum.services;

import ru.practicum.entities.Category;

import java.util.List;

public interface CategoriesService {
    Category saveCategory(Category category);
    void deleteCategory(Long id);
    Category partiallyUpdate(Category category, Long id);
    Category getCategoryById(Long id);
    List<Category> getCategories(int from, int size);
}
