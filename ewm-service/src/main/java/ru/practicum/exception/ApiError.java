package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiError {
    String errors;
    String message;
    String reason;
    String status;
    String timestamp;
}
