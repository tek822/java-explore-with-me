package ru.practicum.location.dto;

import ru.practicum.location.Location;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }
}
