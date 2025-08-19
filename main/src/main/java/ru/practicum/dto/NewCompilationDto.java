package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    List<Long> events;
    Boolean pinned = false;
    @Size(max = 50)
    @NotBlank(message = "Название подборки событий не может быть пустой")
    String title;
}
