package ru.practicum.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class LocationDto {

    @NotNull
    Float lat;

    @NotNull
    Float lon;
}
