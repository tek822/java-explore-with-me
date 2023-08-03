package ru.practicum.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import static ru.practicum.Constants.TITLE_MAX_LENGTH;
import static ru.practicum.Constants.TITLE_MIN_LENGTH;

@Data
@AllArgsConstructor
public class NewAreaDto {
    @NotNull
    @Size(min = TITLE_MIN_LENGTH, max = TITLE_MAX_LENGTH)
    String name;

    @NotNull
    @Valid
    LocationDto location;

    @NotNull
    @Positive
    Float radius;
}

