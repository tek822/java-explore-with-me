package ru.practicum.controller;

import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.StatServiceService;

import java.time.LocalDateTime;

@Slf4j
@Validated
@RestController
public class StatServiceController {
    @Autowired
    private StatServiceService statService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    void addHit(@Validated @RequestBody HitDto hitDto) {
        log.info("POST запрос добавлен hit: {}", hitDto);
        statService.add(hitDto);
    }

    @GetMapping(path = "/stats")
    @ResponseStatus(HttpStatus.OK)
    StatDto getStats(@RequestParam(name = "start", required = true)
                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                     @RequestParam(name = "end", required = true)
                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                     @RequestParam(name = "uris") String[] uris,
                     @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {
        log.info("GET запрос к сервису статистики, start:{}, end:{}, unique{}, uris:{}", start, end, unique, uris);
        return statService.get(start, end, unique, uris);
    }
}
