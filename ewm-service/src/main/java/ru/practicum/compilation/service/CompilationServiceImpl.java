package ru.practicum.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.compilation.mapper.CompilationMapper.toCompilation;
import static ru.practicum.compilation.mapper.CompilationMapper.toCompilationDto;

@Service
public class CompilationServiceImpl implements CompilationService {
    @Autowired
    private CompilationRepository compilationRepository;
    @Autowired
    private EventService eventService;

    @Override
    public CompilationDto add(NewCompilationDto compilationDto) {

        List<Event> events = new ArrayList<>();

        if (!compilationDto.getEvents().isEmpty()) {
            events = eventService.getEventsByIds(compilationDto.getEvents());
            checkEventsIsPresent(compilationDto.getEvents(), events);
        }

        Compilation compilation = toCompilation(compilationDto, events);
        return toCompilationDto(compilationRepository.save(compilation));
    }



    @Override
    public CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilation(compilationRepository, compId);
        if (updateCompilationRequest.getEvents() != null) {
            if (!updateCompilationRequest.getEvents().isEmpty()) {
                List<Event> events = eventService.getEventsByIds(updateCompilationRequest.getEvents());
                checkEventsIsPresent(updateCompilationRequest.getEvents(), events);
                compilation.setEvents(events);
            } else {
                compilation.setEvents(List.of());
            }
        }

        if (updateCompilationRequest.getTitle() != null)
            compilation.setTitle(updateCompilationRequest.getTitle());

        if (updateCompilationRequest.getPinned() != null)
            compilation.setPinned(updateCompilationRequest.getPinned());
        return toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void delete(long compId) {
        Compilation compilation = getCompilation(compilationRepository, compId);
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto getPublicCompilationById(long compId) {
        Compilation compilation = getCompilation(compilationRepository, compId);
        return toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getPublicCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(page).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        }

        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    public static Compilation getCompilation(CompilationRepository compilationRepository, Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Подборка с id: %d, не найдена", compId))
        );
    }

    private static void checkEventsIsPresent(List<Long> ids, List<Event> events) {
        if (events.size() != ids.size()) {
            ids.removeAll(
                events.stream()
                        .map(Event::getId)
                        .collect(Collectors.toList())
            );
            throw new NotFoundException(String.format(
                    "Не найдены события с ids: %s", ids));
        }
    }
}
