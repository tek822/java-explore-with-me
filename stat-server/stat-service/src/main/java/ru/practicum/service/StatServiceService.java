package ru.practicum.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;

public interface StatServiceService {

    void add(HitDto hitDto);

    StatDto get(LocalDateTime start, LocalDateTime end, Boolean unique, String[] uris);
}
