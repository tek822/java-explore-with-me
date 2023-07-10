package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;

@Service
public class StatServiceServiceImpl implements StatServiceService {
    @Override
    public void add(HitDto hitDto) {

    }

    @Override
    public StatDto get(LocalDateTime start, LocalDateTime end, Boolean unique, String[] uris) {
        return null;
    }
}
