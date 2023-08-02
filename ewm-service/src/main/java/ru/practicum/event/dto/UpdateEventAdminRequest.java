package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.EventStateAdminAction;
import ru.practicum.location.dto.LocationDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.Constants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {

    @Size(min = MIN_LENGTH, max = ANNOTATION_MAX_LENGTH)
    String annotation;

    Long category;

    @Size(min = MIN_LENGTH, max = DESCRIPTION_MAX_LENGTH)
    String description;

    @JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;

    @Valid
    LocationDto location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;
    EventStateAdminAction stateAction;

    @Size(min = TITLE_MIN_LENGTH, max = TITLE_MAX_LENGTH)
    String title;
}
