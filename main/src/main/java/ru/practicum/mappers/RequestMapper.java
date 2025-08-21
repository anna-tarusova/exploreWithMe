package ru.practicum.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.entities.Request;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    // Request -> RequestDto
    public static ParticipationRequestDto toDto(Request request) {
        if (request == null) return null;
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getUser().getId());
        dto.setEvent(request.getEvent().getId());
        dto.setCreated(request.getEvent().getCreatedOn());
        dto.setStatus(request.getState());
        return dto;
    }

    // RequestDto -> Request
    public static Request toEntity(ParticipationRequestDto dto) {
        if (dto == null) return null;
        Request request = new Request();
        request.setId(dto.getId());
        request.setState(dto.getStatus());
        return request;
    }
}
