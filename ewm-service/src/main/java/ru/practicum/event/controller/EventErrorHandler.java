package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.event.exception.NewEventDateValidationException;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class EventErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> eventValidationFailed(org.springframework.web.bind.MethodArgumentNotValidException e) {
        log.info("Validation error", e.getMessage());
        return Map.of(
                "status", "BAD_REQUEST",
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> eventTimeValidationFailed(NewEventDateValidationException e) {
        log.info("Validation error", e.getMessage());
        return Map.of(
                "status", "FORBIDDEN",
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
