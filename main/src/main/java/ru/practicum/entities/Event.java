package ru.practicum.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.practicum.entities.enums.State;
import ru.practicum.entities.enums.StateAction;

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

    @Column(nullable = true)
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

    @Column(nullable = false)
    State state;
}
