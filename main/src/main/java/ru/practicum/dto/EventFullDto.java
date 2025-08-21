package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.constants.Constants;
import ru.practicum.entities.enums.EventState;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    String annotation;
    CategoryDto category;
    int confirmedRequests;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    int views;
}
