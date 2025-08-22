package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.constants.Constants;
import ru.practicum.dto.enums.UpdateStateAction;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequestDto {
    @Size(min = 20, max = 2000)
    String annotation;
    Long category;
    @Size(min = 20, max = 7000)
    String description;
    @FutureOrPresent(message = "The date must be in the future or present.")
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime eventDate;
    LocationDto location;
    Boolean paid;
    @Min(value = 1)
    Integer participantLimit;
    Boolean requestModeration;
    UpdateStateAction stateAction;
    @Size(min = 3, max = 120)
    String title;
}
