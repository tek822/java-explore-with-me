package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.Constants.DEFAULT_PAGE_FROM;
import static ru.practicum.Constants.DEFAULT_PAGE_SIZE;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@PathVariable(name = "userId") long userId,
                            @Valid @RequestBody NewEventDto eventDto) {
        log.info("пользователь id: {}, создал событие: {}", userId, eventDto);
        return eventService.add(userId, eventDto);
    }

    @GetMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getPrivateEventById(@PathVariable(name = "userId") long userId,
                                           @PathVariable(name = "eventId") long eventId) {
        log.info("Пользователь с id: {}, запросил информацию о событии с id: {}", userId, eventId);
        return eventService.getPrivateEventById(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getPrivateEventsByInitiatorId(@PathVariable(name = "userId") long userId,
                                                            @PositiveOrZero(message = "Параметр from >= 0")
                                                            @RequestParam(defaultValue = DEFAULT_PAGE_FROM) int from,
                                                            @Positive(message = "Параметр size > 0")
                                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        log.info("Пользователь с id: {}, запросил информацию о собственных событиях", userId);
        return eventService.getPrivateEventsByInitiatorId(userId, from, size);
    }

    @PatchMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updatePrivateEventById(@PathVariable(name = "userId") long userId,
                                               @PathVariable(name = "eventId") long eventId,
                                               @Valid @RequestBody UpdateEventUserRequest eventDto) {
        log.info("Пользователь с id: {}, изменил событие: {}, dto: {}", userId, eventId, eventDto);
        return eventService.updatePrivateEventById(userId, eventId, eventDto);
    }

    @GetMapping(path = "/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable long userId,
                                                     @PathVariable long eventId) {
        log.info("Получение информации о запросах на участие в событии с id: {}, пользователя с id:{}", eventId, userId);
        return requestService.getRequests(userId, eventId);
    }

    @PatchMapping(path = "/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestsStatus(@PathVariable long userId,
                                                               @PathVariable long eventId,
                                                               @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Изменени статуса заявок на участие в событии с id:{}, пользователя: {}", eventId, userId);
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = requestService.updateRequestsStatus(userId, eventId, eventRequestStatusUpdateRequest);
        log.info("StatusUpdateResult: {}", eventRequestStatusUpdateResult);
        return eventRequestStatusUpdateResult;
    }
}

