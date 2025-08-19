package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.entities.Category;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.repositories.CategoriesRepository;
import ru.practicum.repositories.EventRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final EventRepository eventRepository;

    @Override
    public Category saveCategory(Category category) {
        Optional<Category> otherCategory = categoriesRepository.findByName(category.getName());
        if (otherCategory.isPresent() && otherCategory.get().getId() != category.getId()) {
            throw new ConflictException("Category with this name already exists");
        }
        return categoriesRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));
        if (eventRepository.doEventsExists(id)) {
            throw new ConflictException(String.format("There are some events with this category id = %d", id));
        }
        categoriesRepository.deleteById(id);
    }

    @Override
    public Category partiallyUpdate(Category category, Long id) {
        Optional<Category> otherCategory = categoriesRepository.findByName(category.getName());
        if (otherCategory.isPresent() && otherCategory.get().getId() != id) {
            throw new ConflictException("Category with this name already exists");
        }

        Category categoryToUpdate = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));
        if (category.getName() != null) {
            categoryToUpdate.setName(category.getName());
        }
        return categoriesRepository.save(categoryToUpdate);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category %d is not found", id)));
    }

    @Override
    public List<Category> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return categoriesRepository.findAll(pageable).toList();
    }
}
