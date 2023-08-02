package ru.practicum.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToMany
    @JoinTable(name = "compilations_events",
        joinColumns = {@JoinColumn(name = "compilation_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "event_id", referencedColumnName = "id")})
    List<Event> events;

    String title;
    Boolean pinned;
}
