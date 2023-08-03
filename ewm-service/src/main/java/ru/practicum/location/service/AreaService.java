package ru.practicum.location.service;

import ru.practicum.location.dto.NewAreaDto;
import ru.practicum.location.dto.AreaDto;
import ru.practicum.location.dto.UpdateAreaDto;

import java.util.List;

public interface AreaService {

    AreaDto add(NewAreaDto newAreaDto);

    AreaDto update(Long id, UpdateAreaDto update);

    void delete(long locationId);

    AreaDto get(long id);

    List<AreaDto> get(int from, int size);
}
