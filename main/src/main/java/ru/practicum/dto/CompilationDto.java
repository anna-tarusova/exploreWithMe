package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    @NotBlank
    Long id;
    @NotBlank
    Boolean pinned;
    @NotBlank
    String title;
    List<EventShortDto> events;
}
