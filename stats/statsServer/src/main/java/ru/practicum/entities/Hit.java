package ru.practicum.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hits")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hit {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}
