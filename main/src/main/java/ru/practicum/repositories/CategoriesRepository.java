package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entities.Category;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
