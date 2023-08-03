package ru.practicum.location.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.dto.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.dto.NewAreaDto;
import ru.practicum.location.dto.AreaDto;
import ru.practicum.location.dto.UpdateAreaDto;
import ru.practicum.location.model.Area;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.location.repository.AreaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.location.dto.LocationMapper.toAreaDto;

@Slf4j
@Service
@Transactional
public class AreaServiceImpl implements AreaService {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AreaRepository areaRepository;

    @Override
    public AreaDto add(NewAreaDto newAreaDto) {
        Location location = locationRepository.save(
                new Location(null, newAreaDto.getLocation().getLat(), newAreaDto.getLocation().getLon()));
        Area area = areaRepository.save(
                new Area(null, newAreaDto.getName(), newAreaDto.getRadius(), location)
        );
        log.info("Добавлена новая локация: {}", area);
        return toAreaDto(area);
    }

    @Override
    public AreaDto update(Long id, UpdateAreaDto update) {
        Area area = getArea(areaRepository, id);

        if (update.getName() != null)
            area.setName(update.getName());

        if (update.getRadius() != null)
            area.setRadius(update.getRadius());

        if (update.getLocation() != null) {
            area.getLocation().setLat(update.getLocation().getLat());
            area.getLocation().setLon(update.getLocation().getLon());
        }

        return toAreaDto(area);
    }

    @Override
    public void delete(long areaId) {
        log.info("delete: locationId: {}", areaId);
        Area area = getArea(areaRepository, areaId);
        areaRepository.delete(area);
    }

    @Override
    public AreaDto get(long areaId) {
        Area area = getArea(areaRepository, areaId);
        log.info("public pапрос на получение локации с id: {}, area: {}", areaId, area);
        return toAreaDto(area);
    }

    @Override
    public List<AreaDto> get(int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        Page<Area> areas = areaRepository.findAll(page);
        return areas.stream().map(LocationMapper::toAreaDto).collect(Collectors.toList());
    }

    private Area getArea(AreaRepository areaRepository, long areaId) {
        return areaRepository.findById(areaId).orElseThrow(() ->
                new NotFoundException(String.format("Локация с id: %d, не найдена", areaId))
        );
    }
}
