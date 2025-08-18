package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class RequestEventDto {
    @Size(min = 20, max = 2000)
    @NotNull
    @NotBlank
    String annotation;
    @NotNull
    Long category;
    @Size(min = 20, max = 7000)
    @NotBlank(message = "Описание события не может быть пустым")
    String description;
    LocationDto location;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Дата события обязательна")
    @Future(message = "Дата события должна быть в будущем")
    LocalDateTime eventDate;
    Boolean paid;
    @Min(value = 0)
    int participantLimit;
    Boolean requestModeration;
    @Size(min = 3, max = 120)
    @NotBlank(message = "Название события не может быть пустым")
    String title;
}
