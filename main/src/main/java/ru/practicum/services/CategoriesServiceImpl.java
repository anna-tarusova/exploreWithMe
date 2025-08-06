package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.entities.Category;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.repositories.CategoriesRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoriesRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));
        categoriesRepository.deleteById(id);
    }

    @Override
    public Category partiallyUpdate(Category category, Long id) {
        Category categoryToUpdate = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));
        if (category.getName() != null) {
            categoryToUpdate.setName(category.getName());
        }
        return categoriesRepository.save(categoryToUpdate);
    }
}
