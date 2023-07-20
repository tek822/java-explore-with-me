package ru.practicum.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    @Autowired
    private RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable long userId,
                                       @RequestParam long eventId) {
        log.info("Создание запроса на участие в событии с id: {}, пользователя с id: {}", eventId, userId);
        return requestService.add(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> get(@PathVariable long userId) {
        log.info("Вывод запросов на участие пользователя с id: {}", userId);
        return requestService.get(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto update(@PathVariable long userId,
                                          @PathVariable long requestId) {
        log.info("Отмена запроса на участие с id: {}, пользователя с id: {}", requestId, userId);
        return requestService.cancel(userId, requestId);
    }
}
