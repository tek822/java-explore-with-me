package ru.practicum.compilation.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

import static ru.practicum.Constants.COMPILATION_TITLE_MIN_LENGTH;
import static ru.practicum.Constants.COMPILATION_TITLE_MAX_LENGTH;

@Data
public class UpdateCompilationRequest {
    List<Long> events;
    Boolean pinned;

    @Size(min = COMPILATION_TITLE_MIN_LENGTH, max = COMPILATION_TITLE_MAX_LENGTH)
    String title;
}
