package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.Category;
import ru.practicum.location.Location;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String annotation;
    String description;
    @ManyToOne
    Location location;
    @ManyToOne
    Category category;
    @ManyToOne
    User initiator;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    EventState state;
    @Column(columnDefinition = "boolean default false")
    Boolean paid;
    @Column(name = "participant_limit", columnDefinition = "integer default 0")
    Integer participantLimit;
    @Column(name = "request_moderation", columnDefinition = "boolean default true")
    Boolean requestModeration;
}
