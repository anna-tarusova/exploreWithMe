package ru.practicum.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.*;
import ru.practicum.entities.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static Event toEntity(NewEventDto requestEventDto) {
        if (requestEventDto == null) return null;
        Event event = new Event();
        event.setAnnotation(requestEventDto.getAnnotation());
        event.setDescription(requestEventDto.getDescription());
        event.setEventDate(requestEventDto.getEventDate());
        event.setTitle(requestEventDto.getTitle());
        event.setPaid(requestEventDto.getPaid());
        event.setRequestModeration(requestEventDto.getRequestModeration());
        event.setParticipantLimit(requestEventDto.getParticipantLimit());
        event.setEventDate(requestEventDto.getEventDate());
        event.setLocationLat(requestEventDto.getLocation().getLocationLat());
        event.setLocationLon(requestEventDto.getLocation().getLocationLon());
        return event;
    }

    public static EventFullDto toDto(Event event) {
        if (event == null) return null;
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        UserShortDto initiator = new UserShortDto(event.getUser().getId(), event.getUser().getName());
        dto.setInitiator(initiator);
        dto.setAnnotation(event.getAnnotation());
        dto.setTitle(event.getTitle());
        dto.setCategory(CategoryMapper.toDto(event.getCategory()));
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setAnnotation(event.getAnnotation());
        dto.setPaid(event.getPaid());
        LocationDto location = new LocationDto();
        location.setLocationLat(event.getLocationLat());
        location.setLocationLon(event.getLocationLon());
        dto.setLocation(location);
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        return dto;
    }

    public static EventShortDto toShortDto(Event event) {
        if (event == null) return null;
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        UserShortDto initiator = new UserShortDto(event.getUser().getId(), event.getUser().getName());
        dto.setInitiator(initiator);
        dto.setAnnotation(event.getAnnotation());
        dto.setTitle(event.getTitle());
        dto.setCategory(CategoryMapper.toDto(event.getCategory()));
        dto.setEventDate(event.getEventDate());
        dto.setAnnotation(event.getAnnotation());
        dto.setPaid(event.getPaid());
        LocationDto location = new LocationDto();
        location.setLocationLat(event.getLocationLat());
        location.setLocationLon(event.getLocationLon());
        dto.setTitle(event.getTitle());
        return dto;
    }
}
