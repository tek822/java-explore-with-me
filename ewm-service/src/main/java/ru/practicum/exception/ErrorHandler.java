package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = {ForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError forbiddenError(RuntimeException e) {
        log.info("Operation forbidden", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Operation forbidden.")
                .status(HttpStatus.FORBIDDEN.name())
                .errors(getErrors(e))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundError(RuntimeException e) {
        log.info("Entity no found: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.name())
                .errors(getErrors(e))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler(value = {ConstraintViolationException.class,
                                BadRequestException.class,
                                MethodArgumentNotValidException.class,
                                MethodArgumentTypeMismatchException.class,
                                HttpMessageNotReadableException.class})
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

    @ExceptionHandler(value = {ConflictException.class,
            org.springframework.dao.DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflictError(RuntimeException e) {
        log.info("Integrity constraint has been violated: {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
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
