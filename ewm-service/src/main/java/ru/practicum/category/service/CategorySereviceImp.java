package ru.practicum.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.Category;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.dto.CategoryMapper.toCategory;
import static ru.practicum.category.dto.CategoryMapper.toCategoryDto;
import static ru.practicum.util.Util.getCategory;

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
        Category category = getCategory(categoryRepository, categoryDto.getId());
        category.setName(categoryDto.getName());
        return toCategoryDto(categoryRepository.save(category));
    }

    //ToDo check category not empty  before deleting
    @Override
    public void delete(Long categoryId) {
        Category category = getCategory(categoryRepository, categoryId);
        //check not empty
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto get(Long categoryId) {
        Category category = getCategory(categoryRepository, categoryId);
        return toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> get(int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        Page<Category> categories = categoryRepository.findAll(page);
        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }
}
