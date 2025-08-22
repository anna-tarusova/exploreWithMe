package ru.practicum.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.practicum.entities.enums.CommentStatus;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    Event event;

    @CreatedDate
    @Column(nullable = false)
    LocalDateTime created;
    @Column(nullable = false, length = 4000)
    String commentText;

    @Column(nullable = true)
    LocalDateTime updated;

    @Column(nullable = false)
    CommentStatus status;
}
