package ru.practicum.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.*;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.event.service.EventServiceImpl.getEvent;
import static ru.practicum.request.dto.RequestMapper.toParticipationRequestDto;
import static ru.practicum.user.service.UserServiceImpl.getUser;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RequestRepository requestRepository;

    @Override
    public ParticipationRequestDto add(long userId, long eventId) {
        User user = getUser(userRepository, userId);
        Event event = getEvent(eventRepository, eventId);
        //    инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (userId == event.getInitiator().getId()) {
            throw new ConflictException(
                String.format("инициатор события %d не может добавить запрос на участие в своём событии %d", userId, eventId));
        }
        //    нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(
                String.format(
                    "нельзя участвовать в неопубликованном событии, текущий статус события %d, %s", eventId, event.getState().name()));
        }
        //    нельзя добавить повторный запрос (Ожидается код ошибки 409)
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("нельзя добавить повторный запрос на участие");
        }
        //    если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        if (event.getParticipantLimit() != 0) {
            Long confirmedRequests = getConfirmedRequests(event);
            if (event.getParticipantLimit() <= confirmedRequests) {
                throw new ConflictException(String.format(
                    "у события достигнут лимит запросов на участие: %s", event.getParticipantLimit()));
            }
        }

        Request request = new Request(null, event, user, LocalDateTime.now(), RequestStatus.PENDING);
        //    если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> get(long userId) {
        User requester = getUser(userRepository, userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        User requester = getUser(userRepository, requestId);
        Request request = getRequest(requestRepository, requestId);

        if (userId != request.getRequester().getId()) {
            throw new ConflictException(String.format(
                "Пользователь с id: %d, не является создателем заявки: %s", userId, request));
        }

        request.setStatus(RequestStatus.CANCELED);
        return toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(long userId, long eventId) {
        User initiator = getUser(userRepository, userId);
        Event event = getEvent(eventRepository, eventId);
        if (userId != initiator.getId()) {
            throw new ForbiddenException(String.format(
                    "Информация доступна только владельцу c id: %d, id запросившего: %d", initiator.getId(), userId));
        }
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        User owner = getUser(userRepository, userId);
        Event event = getEvent(eventRepository, eventId);

        if (!owner.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException(String.format(
                    "Пользователь с id: %d, не является создателем события с id: %d", userId, eventId));
        }
        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        if (requestsToUpdate.size() != eventRequestStatusUpdateRequest.getRequestIds().size()) {
            eventRequestStatusUpdateRequest.getRequestIds().removeAll(
                    requestsToUpdate.stream()
                        .map(Request::getId)
                        .collect(Collectors.toList())
            );
            throw new NotFoundException(String.format(
                    "Не найдены запросы с ids: %s", eventRequestStatusUpdateRequest.getRequestIds()));
        }
        //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        if (!requestsToUpdate.stream().map(Request::getStatus).allMatch(RequestStatus.PENDING::equals)) {
            throw new ConflictException("статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            requestsToUpdate.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            rejectedRequests.addAll(requestsToUpdate);
        } else {
            //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
            Long confirmedNumber = requests.stream().filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED)).count();
            if (confirmedNumber >= event.getParticipantLimit()) {
                throw new ConflictException(String.format(
                        "Уже достигнут лимит по заявкам на данное событие: %d", event.getParticipantLimit()));
            }
            //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
            for (Request req : requestsToUpdate) {
                if (confirmedNumber < event.getParticipantLimit()) {
                    req.setStatus(RequestStatus.CONFIRMED);
                    confirmedNumber++;
                    confirmedRequests.add(req);
                } else {
                    req.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(req);
                }
            }
        }
        requestRepository.saveAll(requestsToUpdate);
        return new EventRequestStatusUpdateResult(
                confirmedRequests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()),
                rejectedRequests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()));
    }

    @Override
    public Long getConfirmedRequests(Event event) {
        Map<Long, Long> confirmedRequests = getConfirmedRequestsByEvents(List.of(event));
        return confirmedRequests.getOrDefault(event.getId(), 0L);
    }

    @Override
    public Map<Long, Long> getConfirmedRequestsByEvents(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        return getConfirmedRequestsByIds(eventIds);
    }

    private Map<Long, Long> getConfirmedRequestsByIds(List<Long> eventIds) {
        Map<Long, Long> confirmedRequests = new HashMap<>();
        List<ConfirmedRequestsDto> requests = requestRepository.getConfirmedRequests(eventIds);
        requests.forEach(r -> confirmedRequests.put(r.getEventId(), r.getConfirmedRequests()));
        return confirmedRequests;
    }

    public static Request getRequest(RequestRepository requestRepository, Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Заявка с id: %d, не найдена", requestId))
        );
    }
}
