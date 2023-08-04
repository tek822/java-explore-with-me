package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventSortOrder;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.Constants.*;

@Slf4j
@RestController
@RequestMapping("/events")
public class PublicEventController {
    @Autowired
    private EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSortOrder sort,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Positive Integer size,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Float lat,
            @RequestParam(required = false) Float lon,
            @RequestParam(required = false) Float radius,
            HttpServletRequest request
    ) {
        log.info("Запрос событий с параметрами\ntext: {}\ncategories: {}\npaid: {}\nrangeStart: {}\nrangeEnd: {}\n" +
                        "\navailable:{}, \nsort: {},\narea: {},\nlat: {},\nlon: {},\nradius: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, areaId, lat, lon, radius);
        return eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request, areaId, lat, lon, radius);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable long eventId, HttpServletRequest request) {
        log.info("Запрос события с id: {}, клиент: {}, строка запроса: {}",
                eventId, request.getRemoteAddr(), request.getRequestURI());
        return eventService.getPublicEventById(eventId, request);
    }
}
