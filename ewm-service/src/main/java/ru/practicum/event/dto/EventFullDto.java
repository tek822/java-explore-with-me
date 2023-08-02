package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.Constants.DATE_TIME_FORMAT;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    @JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Long views;
}

