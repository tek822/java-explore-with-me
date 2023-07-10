package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Hit;

public interface StatServiceRepository extends JpaRepository<Hit, Long> {
}
