package ru.practicum.model;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@Entity
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String app;
    String uri;
    String ip;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@Column(name = "timestamp")
    LocalDateTime timestamp;
}
