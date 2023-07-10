package ru.practicum.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class HitDto {
    Long id;
    @NotBlank
    String app;
    @NotBlank
    String uri;
    @NotBlank
    String ip;
    @NotBlank
    String timestamp;
}
