package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.Constants.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/events")
public class AdminEventController {
    @Autowired
    private EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAdminEvents(@RequestParam(required = false) List<Long> users,
                            @RequestParam(required = false) List<EventState> states,
                            @RequestParam(required = false) List<Long> categories,
                            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                            @PositiveOrZero(message = "Параметр from >= 0")
                            @RequestParam(defaultValue = DEFAULT_PAGE_FROM) Integer from,
                            @Positive(message = "Параметр size > 0")
                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer size) {
        log.info("Администратор запросил события пользователей {}, в категориях {} и состояниях {}", users, categories, states);
        return eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable(name = "eventId") Long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Обновление события с id: {}, администратором, параметры: {}", eventId, updateEventAdminRequest);
        return eventService.updateAdminEventById(eventId, updateEventAdminRequest);
    }
}
