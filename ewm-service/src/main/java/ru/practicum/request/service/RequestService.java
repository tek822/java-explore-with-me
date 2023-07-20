package ru.practicum.request.service;

import ru.practicum.event.model.Event;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {
    ParticipationRequestDto add(long userId, long eventId);

    List<ParticipationRequestDto> get(long userId);

    ParticipationRequestDto cancel(long userId, long requestId);

    List<ParticipationRequestDto> getRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    Long getConfirmedRequests(Event event);

    Map<Long, Long> getConfirmedRequestsByEvents(List<Event> events);
}
