package ru.practicum.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {
    Long id;
    List<EventShortDto> events;
    Boolean pinned;
    String title;
}
