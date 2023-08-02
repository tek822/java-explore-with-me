package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class CategoryDto {
    Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    String name;
}
