package ru.practicum.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.location.model.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {
}
