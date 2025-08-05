package ru.practicum.mappers;

import ru.practicum.dto.CategoryDto;
import ru.practicum.entities.Category;

public class CategoryMapper {

    // Category -> CategoryDto
    public static CategoryDto toDto(Category category) {
        if (category == null) return null;
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    // CategoryDto -> Category
    public static Category toEntity(CategoryDto dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
