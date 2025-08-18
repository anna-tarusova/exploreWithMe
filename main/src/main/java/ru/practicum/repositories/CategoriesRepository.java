package ru.practicum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.entities.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c")
    Page<Category> getCategories(Pageable pageable);
}
