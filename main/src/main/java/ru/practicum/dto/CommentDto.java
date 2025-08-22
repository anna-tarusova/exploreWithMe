package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.constants.Constants;
import ru.practicum.entities.enums.CommentStatus;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    UserShortDto author;
    EventShortDto event;
    String commentText;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime created;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime updated;
    CommentStatus status;
}
