package ru.practicum.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSortOrder;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.EventStateAdminAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Area;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.AreaRepository;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.stats.StatsService;
import ru.practicum.user.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.Constants.ADMIN_TIME_GAP;
import static ru.practicum.Constants.USER_TIME_GAP;
import static ru.practicum.category.service.CategoryServiceImpl.getCategory;
import static ru.practicum.event.dto.EventMapper.newEventDtoToEvent;
import static ru.practicum.event.dto.EventMapper.toEventFullDto;
import static ru.practicum.location.service.AreaServiceImpl.getArea;
import static ru.practicum.request.service.RequestServiceImpl.getConfirmedRequests;
import static ru.practicum.request.service.RequestServiceImpl.getConfirmedRequestsByEvents;
import static ru.practicum.user.service.UserServiceImpl.getUser;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CompilationRepository compilationRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private StatsService statsService;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private AreaRepository areaRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public EventFullDto add(long userId, NewEventDto eventDto) {
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(USER_TIME_GAP))) {
            //  в спецификации должен быть 409, но тесты требуют 400 !
            throw new BadRequestException(String.format("До события должно быть не менее %d часов", USER_TIME_GAP));
        }
        User initiator = getUser(userRepository, userId);
        Category category = getCategory(categoryRepository, eventDto.getCategory());
        Location location = locationRepository.save(
                new Location(null, eventDto.getLocation().getLat(), eventDto.getLocation().getLon()));
        Event event = newEventDtoToEvent(eventDto);
        event.setInitiator(initiator);
        event.setLocation(location);
        event.setCategory(category);
        event = eventRepository.save(event);
        log.info("Создано новое событие: {}", event);
        return toEventFullDto(event, 0L, 0L);
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        validateDates(rangeStart, rangeEnd);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate criteria = cb.conjunction();

        if (users != null && !users.isEmpty())
            criteria = cb.and(criteria, root.get("initiator").in(users));

        if (states != null && !states.isEmpty())
            criteria = cb.and(criteria, root.get("state").in(states));

        if (categories != null && !categories.isEmpty())
            criteria = cb.and(criteria, root.get("category").in(categories));

        if (rangeStart != null)
            criteria = cb.and(criteria, cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));

        if (rangeEnd != null)
            criteria = cb.and(criteria, cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));

        query.select(root).where(criteria);
        List<Event> events = entityManager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();

        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = getConfirmedRequestsByEvents(requestRepository, events);

        List<EventFullDto> eventDtos = events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());

        for (EventFullDto eventDto : eventDtos) {
            eventDto.setViews(views.getOrDefault(eventDto.getId(), 0L));
            eventDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventDto.getId(), 0L));
        }

        return eventDtos;
    }

    @Override
    @Transactional
    public EventFullDto updateAdminEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = getEvent(eventRepository, eventId);

        if (updateEventAdminRequest.getEventDate() != null) {
            validateEventDate(updateEventAdminRequest.getEventDate(), ADMIN_TIME_GAP);
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getAnnotation() != null)
            event.setAnnotation(updateEventAdminRequest.getAnnotation());

        if (updateEventAdminRequest.getCategory() != null)
            event.setCategory(getCategory(categoryRepository, updateEventAdminRequest.getCategory()));

        if (updateEventAdminRequest.getDescription() != null)
            event.setDescription(updateEventAdminRequest.getDescription());

        if (updateEventAdminRequest.getLocation() != null) {
            // @Transactional должно обновить связанные объекты
            event.getLocation().setLat(updateEventAdminRequest.getLocation().getLat());
            event.getLocation().setLon(updateEventAdminRequest.getLocation().getLon());
        }

        if (updateEventAdminRequest.getPaid() != null)
            event.setPaid(updateEventAdminRequest.getPaid());

        if (updateEventAdminRequest.getParticipantLimit() != null)
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());

        if (updateEventAdminRequest.getRequestModeration() != null)
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ConflictException(
                        String.format("Изменить можно только события в статусе PENDING, текущий статус: %s", event.getState().name()));
            }

            if (updateEventAdminRequest.getStateAction() == EventStateAdminAction.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                event.setState(EventState.CANCELED);
            }

        }

        if (updateEventAdminRequest.getTitle() != null)
            event.setTitle(updateEventAdminRequest.getTitle());

        EventFullDto eventDto = toEventFullDto(eventRepository.save(event));
        Map<Long, Long> views = statsService.getViews(List.of(event));
        Map<Long, Long> confirmedRequests = getConfirmedRequestsByEvents(requestRepository, List.of(event));

        eventDto.setViews(views.getOrDefault(eventId, 0L));
        eventDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventId, 0L));

        return eventDto;
    }

    @Override
    public EventFullDto getPrivateEventById(long userId, long eventId) {
        User user = getUser(userRepository, userId);
        Event event = getEvent(eventRepository, eventId);
        Long views = statsService.getViews(List.of(event)).getOrDefault(eventId, 0L);
        Long confirmedRequests = getConfirmedRequestsByEvents(requestRepository, List.of(event)).getOrDefault(eventId, 0L);
        return toEventFullDto(event, views, confirmedRequests);
    }

    @Override
    public List<EventShortDto> getPrivateEventsByInitiatorId(long userId, int from, int size) {
        User user = getUser(userRepository, userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));

        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = getConfirmedRequestsByEvents(requestRepository, events);

        List<EventShortDto> eventDtos = events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());

        for (EventShortDto eventDto : eventDtos) {
            eventDto.setViews(views.getOrDefault(eventDto.getId(), 0L));
            eventDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventDto.getId(), 0L));
        }
        return eventDtos;
    }

    @Override
    @Transactional
    public EventFullDto updatePrivateEventById(long userId, long eventId, UpdateEventUserRequest eventDto) {
        User user = getUser(userRepository, userId);
        Event event = getEvent(eventRepository, eventId);

        if (userId != event.getInitiator().getId()) {
            throw new ForbiddenException(String.format(
                "Пользователь с id: %d, не является создателем события с id: %d, операция запрещена", userId, eventId));
        }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(
                    "Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

        validateEventDate(event.getEventDate(), USER_TIME_GAP);

        if (eventDto.getEventDate() != null) {
            validateEventDate(eventDto.getEventDate(), USER_TIME_GAP);
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getAnnotation() != null)
            event.setAnnotation(eventDto.getAnnotation());

        if (eventDto.getCategory() != null)
            event.setCategory(getCategory(categoryRepository, eventDto.getCategory()));

        if (eventDto.getDescription() != null)
            event.setDescription(eventDto.getDescription());

        if (eventDto.getEventDate() != null)
            event.setEventDate(eventDto.getEventDate());

        if (eventDto.getLocation() != null) {
            event.getLocation().setLat(eventDto.getLocation().getLat());
            event.getLocation().setLon(eventDto.getLocation().getLon());
        }

        if (eventDto.getPaid() != null)
            event.setPaid(eventDto.getPaid());

        if (eventDto.getParticipantLimit() != null)
            event.setParticipantLimit(eventDto.getParticipantLimit());


        if (eventDto.getRequestModeration() != null)
            event.setRequestModeration(eventDto.getRequestModeration());


        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }

        Long views = statsService.getViews(List.of(event)).getOrDefault(eventId, 0L);
        Long confirmedRequests = getConfirmedRequestsByEvents(requestRepository, List.of(event)).getOrDefault(eventId, 0L);

        return toEventFullDto(eventRepository.save(event), views, confirmedRequests);
    }

    @Override
    public EventFullDto getPublicEventById(long eventId, HttpServletRequest request) {
        Event event = getEvent(eventRepository, eventId);
        //событие должно быть опубликовано
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Опубликованного события с id: %s, не найдено.", eventId));
        }
        //информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
        Long views = statsService.getViews(List.of(event)).getOrDefault(eventId, 0L);
        Long confirmedRequests = getConfirmedRequests(requestRepository,event);
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        statsService.addHit(request);
        return toEventFullDto(event, views, confirmedRequests);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               EventSortOrder sort,
                                               Integer from,
                                               Integer size,
                                               HttpServletRequest request,
                                               Long areaId,
                                               Float lat,
                                               Float lon,
                                               Float radius) {
        validateDates(rangeStart, rangeEnd);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Request> requestRoot = subquery.from(Request.class);
        Join<Request, Event> join = requestRoot.join("event");

        Predicate criteria = cb.conjunction();

        // текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
        if (text != null && !text.isBlank()) {
            String pattern = "%" + text.toLowerCase() + "%";
            criteria = cb.and(criteria,
                    cb.or(
                            cb.like(cb.lower(root.get("annotation")), pattern),
                            cb.like(cb.lower(root.get("description")), pattern)
                    ));
        }

        if (areaId != null) {
            Area area = getArea(areaRepository, areaId);
            lat = area.getLocation().getLat();
            lon = area.getLocation().getLon();
            radius = area.getRadius();

            criteria = cb.and(criteria,
                    cb.greaterThanOrEqualTo(
                            cb.literal(radius),
                            cb.function("distance", Float.class,
                                    cb.literal(lat),
                                    cb.literal(lon),
                                    root.<Location>get("location").get("lat"),
                                    root.<Location>get("location").get("lon")
                            )
                    )
            );
        } else {
            if (lat != null && lon != null && radius != null) {
                criteria = cb.and(criteria,
                        cb.greaterThanOrEqualTo(
                                cb.literal(radius),
                                cb.function("distance", Float.class,
                                        cb.literal(lat),
                                        cb.literal(lon),
                                        root.<Location>get("location").get("lat"),
                                        root.<Location>get("location").get("lon")
                                )
                        )
                );

            } else if (lat != null || lon != null || radius != null) {
                throw new BadRequestException(String.format(
                        "Для поиска должны быть заданы все параметры локации: lat=%d, lon=%d, radius=%d", lat, lon, radius));
            }
        }

        if (categories != null && !categories.isEmpty())
            criteria = cb.and(criteria, root.get("category").in(categories));

        if (paid != null)
            criteria = cb.and(criteria, cb.equal(root.get("paid"), paid));

        // если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
        if (rangeStart == null && rangeEnd == null) {
            criteria = cb.and(criteria, cb.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.now()));
        } else {
            if (rangeStart != null)
                criteria = cb.and(criteria, cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));

            if (rangeEnd != null)
                criteria = cb.and(criteria, cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        // это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
        criteria = cb.and(criteria, cb.equal(root.get("state"), EventState.PUBLISHED));

        if (onlyAvailable) {
            criteria = cb.and(criteria,
                    cb.or(
                            cb.equal(root.get("participantLimit"), 0L),
                            cb.and(
                                    cb.notEqual(root.get("participantLimit"), 0L),
                                    cb.greaterThan(
                                            root.get("participantLimit"),
                                            subquery.select(cb.count(requestRoot.get("id")))
                                                    .where(cb.equal(requestRoot.get("status"), RequestStatus.CONFIRMED))
                                                    .where(cb.equal(requestRoot.get("event").get("id"), join.get("id")))
                                    )
                            )
                    ));
        }

        query.select(root).where(criteria);
        List<Event> events = entityManager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();

        // информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = getConfirmedRequestsByEvents(requestRepository, events);

        List<EventShortDto> eventDtos = events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());

        for (EventShortDto eventDto : eventDtos) {
            eventDto.setViews(views.getOrDefault(eventDto.getId(), 0L));
            eventDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventDto.getId(), 0L));
        }

        //Вариант сортировки: по дате события или по количеству просмотров
        if (sort != null) {
            if (sort == EventSortOrder.EVENT_DATE) {
                eventDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
            } else if (sort == EventSortOrder.VIEWS) {
                eventDtos.sort(Comparator.comparing(EventShortDto::getViews));
            }
        }

        // информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        statsService.addHit(request);
        return eventDtos;
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException(
                    String.format("Ошибка при задании временного интервала start: %s, end: %s", start, end));
        }
    }

    public static Event getEvent(EventRepository eventRepository, long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
            new NotFoundException(String.format("Событие с id: %s, не найдено.", eventId))
        );
    }

    private void validateEventDate(LocalDateTime newEventDate, Long timeGap) {
        if (newEventDate != null && newEventDate.isBefore(LocalDateTime.now().plusHours(timeGap))) {
            // в спецификации 409, но тесты требуют 400 !
            throw new BadRequestException(
                String.format("дата начала изменяемого события должна быть не ранее чем за %d час(a) от даты публикации\n" +
                    "newEventDate: %s", timeGap, newEventDate));
        }
    }

    private Location getLocation(LocationDto locationDto) {
        return locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon())
            .orElseGet(() ->
                locationRepository.save(
                    new Location(null, locationDto.getLat(), locationDto.getLon())
                )
            );
    }
}
