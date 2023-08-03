package ru.practicum.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AreaDto {
    Long id;
    String name;
    LocationDto location;
    Float radius;
}
