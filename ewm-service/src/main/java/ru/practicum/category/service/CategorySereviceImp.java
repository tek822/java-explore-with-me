package ru.practicum.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.category.Category;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.repository.CategoryRepository;

import java.util.List;

import static ru.practicum.category.dto.CategoryMapper.toCategory;
import static ru.practicum.category.dto.CategoryMapper.toCategoryDto;

@Service
public class CategorySereviceImp implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryDto add(CategoryDto categoryDto) {
        Category category = toCategory(categoryDto);
        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        return null;
    }

    @Override
    public void delete(Long categoryId) {

    }

    @Override
    public CategoryDto get(Long categoryId) {
        return null;
    }

    @Override
    public List<CategoryDto> get(int from, int size) {
        return null;
    }
}
