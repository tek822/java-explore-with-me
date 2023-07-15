package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.model.HitMapper.dtoToHit;

@Service
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    @Autowired
    private StatsRepository statsRepository;

    @Override
    @Transactional
    public void add(HitDto hitDto) {
        statsRepository.save(dtoToHit(hitDto));
    }

    @Override
    public List<StatsDto> get(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris) {
        List<StatsDto> hits = new ArrayList<>();
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                hits.addAll(statsRepository.getAllUniqueStats(start, end));
            } else {
                hits.addAll(statsRepository.getAllStats(start, end));
            }
        } else {
            if (unique) {
                hits.addAll(statsRepository.getUniqueStats(start, end, uris));
            } else {
                hits.addAll(statsRepository.getStats(start, end, uris));
            }
        }
        return hits;
    }
}
