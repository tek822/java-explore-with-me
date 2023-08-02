package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.BadRequestException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = {BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequestError(RuntimeException e) {
        log.info("Constraint violation: {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .errors(getErrors(e))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    private String getErrors(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
