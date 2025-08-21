package ru.practicum.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Data
@Entity
@Table(name = "compilations")
@EntityListeners(AuditingEntityListener.class)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean pinned;

    @Column(nullable = false, length = 120)
    private String title;

    // Связь многие-ко-многим с Event
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events;
}
