package ru.practicum.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.practicum.entities.enums.EventState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, length = 2000)
    String annotation;
    @Column(nullable = false, length = 7000)
    String description;
    @Column(nullable = false)
    LocalDateTime eventDate;
    @Column(nullable = false)
    Double locationLat;
    @Column(nullable = false)
    Double locationLon;
    @Column(nullable = false)
    Boolean paid;
    @Column(nullable = false)
    int participantLimit;
    @Column(nullable = false)
    Boolean requestModeration;
    @Column(nullable = false, length = 120)
    String title;

    @Column()
    LocalDateTime publishedOn;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    LocalDateTime createdOn;

    // Внешний ключ на User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    // Внешний ключ на Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    private Set<Compilation> compilations = new HashSet<>();

    @Column(nullable = false)
    EventState state;

    @ManyToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<Request> requests = new ArrayList<>();
}
