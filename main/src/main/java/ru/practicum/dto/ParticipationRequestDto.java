package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.entities.enums.RequestState;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss:SSS")
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    RequestState status;
}
