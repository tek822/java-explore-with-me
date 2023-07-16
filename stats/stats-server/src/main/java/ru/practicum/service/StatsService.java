package ru.practicum.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void add(HitDto hitDto);

    List<StatsDto> get(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris);
}
