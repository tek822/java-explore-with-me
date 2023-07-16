package ru.practicum.model;

import ru.practicum.dto.HitDto;

public class HitMapper {
    public static Hit dtoToHit(HitDto dto) {
        return new Hit(null, dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
    }
}
