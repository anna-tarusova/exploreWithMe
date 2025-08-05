package ru.practicum.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "categories")
public class Category {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
}
