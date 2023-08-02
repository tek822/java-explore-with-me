package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = TITLE_MIN_LENGTH, max = TITLE_MAX_LENGTH)
    String title;

    @NotBlank
    @Size(min = MIN_LENGTH, max = ANNOTATION_MAX_LENGTH)
    String annotation;

    @NotBlank
    @Size(min = MIN_LENGTH, max = DESCRIPTION_MAX_LENGTH)
    String description;

    @NotNull
    LocationDto location;

    @NotNull
    @Positive
    Long category;

    @NotNull
    @Future
    @JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;

    Boolean paid = false; // default false
    Integer participantLimit = 0; // default 0
    Boolean requestModeration = true; // default true
}
