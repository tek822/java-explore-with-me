package ru.practicum.location.dto;

import ru.practicum.location.model.Location;
import ru.practicum.location.model.Area;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }

    public static AreaDto toAreaDto(Area area) {
        return new AreaDto(area.getId(), area.getName(),
                toLocationDto(area.getLocation()),
                area.getRadius());
    }
}
