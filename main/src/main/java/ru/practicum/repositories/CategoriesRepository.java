package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entities.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {

}
