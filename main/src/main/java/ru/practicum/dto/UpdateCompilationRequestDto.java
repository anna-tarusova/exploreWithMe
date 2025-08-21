package ru.practicum.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequestDto {
    Boolean pinned;
    @Size(max = 50)
    String title;
    List<Long> events;
}
