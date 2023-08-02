package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.dto.ConfirmedRequestsDto;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    Optional<Request> findByRequesterIdAndEventId(Long requestId, Long userId);

    @Query("SELECT new ru.practicum.request.dto.ConfirmedRequestsDto(req.event.id, count(req.id)) FROM Request AS req " +
           "WHERE req.event.id IN ?1 AND req.status='CONFIRMED' GROUP BY req.event.id")
    List<ConfirmedRequestsDto> getConfirmedRequests(List<Long> eventIds);

    List<Request> findAllByEventId(long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);
}
