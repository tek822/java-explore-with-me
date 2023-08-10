package ru.practicum.location.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.location.dto.AreaDto;
import ru.practicum.location.dto.NewAreaDto;
import ru.practicum.location.dto.UpdateAreaDto;
import ru.practicum.location.service.AreaService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.Constants.DEFAULT_PAGE_FROM;
import static ru.practicum.Constants.DEFAULT_PAGE_SIZE;

@Slf4j
@Validated
@RestController
public class LocationController {
    @Autowired
    AreaService areaService;

    @PostMapping("/admin/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public AreaDto add(@Valid @RequestBody NewAreaDto newAreaDto) {
        log.info("POST запрос на добавление локации: {}", newAreaDto);
        return areaService.add(newAreaDto);
    }

    @PatchMapping(path = "/admin/locations/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public AreaDto update(@PathVariable(name = "locationId") long locationId,
                          @RequestBody UpdateAreaDto updateAreaDto) {
        log.info("PATCH запрос на обновление локации с id: {}, update: {}", locationId, updateAreaDto);
        return areaService.update(locationId, updateAreaDto);
    }

    @DeleteMapping(path = "/admin/locations/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "locationId") long locationId) {
        log.info("DELETE запрос на удаление локации с id: {}", locationId);
        areaService.delete(locationId);
    }

    @GetMapping(path = "/locations/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public AreaDto get(@PathVariable(name = "locationId") long locationId) {
        log.info("PUBLIC GET запрос на получение локации с id: {}", locationId);
        return areaService.get(locationId);
    }

    @GetMapping("/locations")
    @ResponseStatus(HttpStatus.OK)
    public List<AreaDto> get(
            @PositiveOrZero(message = "Параметр from >= 0")
            @RequestParam(name = "from", defaultValue = DEFAULT_PAGE_FROM) int from,
            @Positive(message = "Параметр size > 0")
            @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        log.info("Запрос локаций с параметрами from: {}, size: {}", from, size);
        return areaService.get(from, size);
    }
}
