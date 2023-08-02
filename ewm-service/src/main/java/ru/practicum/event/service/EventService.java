package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.model.EventSortOrder;
import ru.practicum.event.model.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto add(long userId, NewEventDto eventDto);

    List<EventFullDto> getAdminEvents(List<Long> users,
                                      List<EventState> states,
                                      List<Long> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      int from,
                                      int size);

    EventFullDto updateAdminEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto getPrivateEventById(long userId, long eventId);

    List<EventShortDto> getPrivateEventsByInitiatorId(long userId, int from, int size);

    EventFullDto updatePrivateEventById(long userId, long eventId, UpdateEventUserRequest eventDto);

    EventFullDto getPublicEventById(long eventId, HttpServletRequest request);

    List<EventShortDto> getPublicEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        EventSortOrder sort,
                                        Integer from,
                                        Integer size,
                                        HttpServletRequest request);
}
