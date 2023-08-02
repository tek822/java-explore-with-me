package ru.practicum.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.Constants.DEFAULT_PAGE_FROM;
import static ru.practicum.Constants.DEFAULT_PAGE_SIZE;

@Slf4j
@RestController
@Validated
public class CompilationController {
    @Autowired
    private CompilationService compilationService;

    @PostMapping(path = "/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Добавлена подборка: {}", compilationDto);
        return compilationService.add(compilationDto);
    }

    @PatchMapping(path = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(
            @PathVariable(name = "compId", required = true) long compId,
            @Valid @RequestBody UpdateCompilationRequest compilationDto) {
        log.info("Обновлена подборка: {}", compilationDto);
        return compilationService.update(compId, compilationDto);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "compId", required = true) long compId) {
        log.info("Удалена подборка с id: {}", compId);
        compilationService.delete(compId);
    }

    @GetMapping(path = "/compilations")
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> get(@RequestParam(required = false) Boolean pinned,
                                    @PositiveOrZero(message = "Параметр from >= 0")
                                    @RequestParam(name = "from", defaultValue = DEFAULT_PAGE_FROM) int from,
                                    @Positive(message = "Параметр size > 0")
                                    @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        log.info("Запршены подборки from: {}, size: {}", from, size);
        return compilationService.getPublicCompilations(pinned, from, size);
    }

    @GetMapping(path = "/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto get(@PathVariable(name = "compId", required = true) long compId) {
        log.info("Запршена подборка c id: {}", compId);
        return compilationService.getPublicCompilationById(compId);
    }
}
