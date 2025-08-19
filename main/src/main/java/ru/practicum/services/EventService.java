package ru.practicum.services;

import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.UpdateEventUserRequestDto;
import ru.practicum.entities.Event;
import ru.practicum.entities.enums.Sort;
import ru.practicum.entities.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event saveEvent(Event event);

    Event getEventByUserIdAndId(Long eventId, Long id);

    List<Event> getEventsByUserId(Long userId, int from, int size);

    List<Event> getEvents(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart,
                          LocalDateTime rangeEnd, int from, int size);

    List<EventShortDto> getEventsPublic(
            String text,
            List<Long> users,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            Sort sort,
            int from,
            int size);

    Event getById(Long id);

    Event updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto dto);

    int countConfirmedRequests(Long eventId);
}
