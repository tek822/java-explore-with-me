package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
public class StatsController {
    @Autowired
    private StatsService statService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    void addHit(@Validated @RequestBody HitDto hitDto) {
        log.info("POST запрос добавлен hit: {}", hitDto);
        statService.add(hitDto);
    }

    @GetMapping(path = "/stats")
    @ResponseStatus(HttpStatus.OK)
    List<StatsDto> getStats(@RequestParam(name = "start", required = true)
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                            @RequestParam(name = "end", required = true)
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                            @RequestParam(name = "uris", required = false) List<String> uris,
                            @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("GET запрос к сервису статистики, start:{}, end:{}, unique{}, uris:{}", start, end, unique, uris);
        return statService.get(start, end, unique, uris);
    }
}
