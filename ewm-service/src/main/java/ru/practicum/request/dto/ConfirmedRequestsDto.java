package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmedRequestsDto {
    Long eventId;
    Long confirmedRequests;
}
