package ru.practicum.category.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.dto.CategoryMapper.toCategory;
import static ru.practicum.category.dto.CategoryMapper.toCategoryDto;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(rollbackFor = { ConstraintViolationException.class, Exception.class, RuntimeException.class })
    public CategoryDto add(CategoryDto categoryDto) {
        Category category = toCategory(categoryDto);
        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(CategoryDto categoryDto) {
        Category category = getCategory(categoryRepository, categoryDto.getId());
        category.setName(categoryDto.getName());
        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        Category category = getCategory(categoryRepository, categoryId);
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

    public static Category getCategory(CategoryRepository categoryRepository, long categoryID) {
        return  categoryRepository.findById(categoryID).orElseThrow(() ->
                new NotFoundException("Категория с id: " + categoryID + " не найдена.")
        );
    }
}
