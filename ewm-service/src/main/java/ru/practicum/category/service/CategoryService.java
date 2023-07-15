package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto add(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(Long categoryId);

    CategoryDto get(Long categoryId);

    List<CategoryDto> get(int from, int size);
}
