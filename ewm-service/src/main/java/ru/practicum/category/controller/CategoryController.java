package ru.practicum.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.Constants.DEFAULT_PAGE_FROM;
import static ru.practicum.Constants.DEFAULT_PAGE_SIZE;

@Slf4j
@RestController
@Validated
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping(path = "/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Добавлена категория: {}", categoryDto.getName());
        return categoryService.add(categoryDto);
    }

    @PatchMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable(name = "catId", required = true) Long catId,
                              @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(catId);
        log.info("Обновлена категория: {}", categoryDto);
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "catId", required = true) Long catId) {
        log.info("Удалена категория с id: {}", catId);
        categoryService.delete(catId);
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> get(
            @PositiveOrZero(message = "Параметр from >= 0")
            @RequestParam(name = "from", defaultValue = DEFAULT_PAGE_FROM) int from,
            @Positive(message = "Параметр size > 0")
            @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        log.info("Запрос категорий с параметрами from: {}, size: {}", from, size);
        return categoryService.get(from, size);
    }

    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto get(@PathVariable(name = "catId", required = true) Long catId) {
        log.info("Запрошена категория с id: {}", catId);
        return categoryService.get(catId);
    }
}
