package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto add(NewCompilationDto compilationDto);

    CompilationDto update(long compId, UpdateCompilationRequest compilationDto);

    void delete(long compId);

    CompilationDto getPublicCompilationById(long compId);

    List<CompilationDto> getPublicCompilations(Boolean pinned, int from, int size);
}
