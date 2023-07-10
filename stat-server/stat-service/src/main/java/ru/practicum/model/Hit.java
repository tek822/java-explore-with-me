package ru.practicum.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
public class Hit {
    Long id;
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}
