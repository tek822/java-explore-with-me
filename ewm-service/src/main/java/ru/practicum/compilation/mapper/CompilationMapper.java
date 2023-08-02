package ru.practicum.compilation.mapper;

import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.event.dto.EventMapper.toEventShortDto;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(
                        compilation.getEvents().stream().map(EventMapper::toEventShortDto).collect(Collectors.toList())
                )
                .build();
    }
}
