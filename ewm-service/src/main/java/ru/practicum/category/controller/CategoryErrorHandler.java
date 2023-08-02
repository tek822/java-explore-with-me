package ru.practicum.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.category.exception.CategoryNotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CategoryErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> categoryNotFound(CategoryNotFoundException e) {
        log.info(e.getMessage());
        return Map.of(
                "status", "NOT_FOUND",
                "reason", "The required object was not found.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    // SQL UNIQUE constraint violation
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> constraintViolationError(ConstraintViolationException e) {
        log.info(e.getMessage());
        return Map.of(
                "status", "CONFLICT",
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    // @Validated constraint violation in controller
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> categoryValidationFailed(org.springframework.web.bind.MethodArgumentNotValidException e) {
        log.info("Validation error", e.getMessage());
        return Map.of(
                "status", "BAD_REQUEST",
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> emptyBodyError(org.springframework.http.converter.HttpMessageNotReadableException e) {
        log.info("Validation error", e.getMessage());
        return Map.of(
                "status", "BAD_REQUEST",
                "reason", "Request body is empty.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

}
