package ru.practicum.event.exception;

public class NewEventDateValidationException extends RuntimeException {
    public NewEventDateValidationException(String message) {
        super(message);
    }
}
