package ru.practicum.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "categories")
public class Category {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(unique = true)
    String name;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Event> events = new HashSet<>();
}
