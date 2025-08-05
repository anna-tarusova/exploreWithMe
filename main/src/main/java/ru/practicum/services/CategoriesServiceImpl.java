package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.entities.Category;
import ru.practicum.repositories.CategoriesRepository;

@Component
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoriesRepository.save(category);
    }
}
