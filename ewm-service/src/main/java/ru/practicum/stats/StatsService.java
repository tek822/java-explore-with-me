package ru.practicum.stats;

import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface StatsService {

    void addHit(HttpServletRequest request);

    Map<Long, Long> getViews(List<Event> events);
}
