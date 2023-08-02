package ru.practicum.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.event.model.Event;
import ru.practicum.exception.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    private StatsClient statsClient;
    @Value("${application.name}")
    private String applicationName;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void addHit(HttpServletRequest request) {
        log.info("addHit: URI={}, RemoteAddr={}", request.getRequestURI(), request.getRemoteAddr());
        statsClient.addHit(
                new HitDto(null, applicationName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();

        if (events == null || events.isEmpty())
            return views;

        List<String> uris = events.stream()
                .map(e -> String.format("/events/%d", e.getId()))
                .collect(Collectors.toList());

        Optional<LocalDateTime> rangeStart = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (rangeStart.isPresent()) {
            ResponseEntity<Object> response = statsClient.getStats(rangeStart.get(), LocalDateTime.now(), uris, true);
            try {
                List<StatsDto> stats = objectMapper.readValue(
                        objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<StatsDto>>() {});
                for (StatsDto stat : stats) {
                    Long eventId = Long.parseLong(stat.getUri().split("/", 0)[2]);
                    views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
                }
            } catch (JsonProcessingException e) {
                log.info("Ошибка преобразования json ответа сервера статистики: {}", response.getBody());
                throw new BadRequestException(String.format(
                        "Ошибка преобразования json ответа сервера статистики: %s", response.getBody()));
            }
        }
        return views;
    }
}
