package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.request.model.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.Constants.DATE_TIME_FORMAT;

@Data
@Builder
public class ParticipationRequestDto {
    @JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    RequestStatus status;
}
