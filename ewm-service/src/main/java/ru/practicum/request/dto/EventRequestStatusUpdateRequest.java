package ru.practicum.request.dto;

import lombok.Data;
import ru.practicum.request.model.RequestStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    @NotNull
    @NotEmpty
    List<Long> requestIds;
    @NotNull
    RequestStatus status;
}
