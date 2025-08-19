package ru.practicum.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.entities.enums.RequestStateAction;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequestDto {
    List<Long> requestIds;
    RequestStateAction status;
}
