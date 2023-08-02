package ru.practicum.compilation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.Constants.COMPILATION_TITLE_MAX_LENGTH;
import static ru.practicum.Constants.COMPILATION_TITLE_MIN_LENGTH;

@Data
public class NewCompilationDto {
    List<Long> events = new ArrayList<>();
    Boolean pinned = false;

    @NotBlank
    @Size(min = COMPILATION_TITLE_MIN_LENGTH, max = COMPILATION_TITLE_MAX_LENGTH)
    String title;
}
