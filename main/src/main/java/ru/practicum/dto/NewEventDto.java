package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.constants.Constants;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @Size(min = 20, max = 2000)
    @NotBlank
    String annotation;
    @NotNull
    Long category;
    @Size(min = 20, max = 7000)
    @NotBlank(message = "Описание события не может быть пустым")
    String description;
    LocationDto location;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    @NotNull(message = "Дата события обязательна")
    @Future(message = "Дата события должна быть в будущем")
    LocalDateTime eventDate;
    Boolean paid = false;
    @Min(value = 0)
    int participantLimit = 0;
    Boolean requestModeration = true;
    @Size(min = 3, max = 120)
    @NotBlank(message = "Название события не может быть пустым")
    String title;
}
