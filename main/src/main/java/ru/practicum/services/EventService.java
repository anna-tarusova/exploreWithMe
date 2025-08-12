package ru.practicum.services;

import ru.practicum.entities.Event;
import ru.practicum.entities.enums.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event saveEvent(Event event);
    List<Event> getEventsByUserId(Long userId, int from, int size);
    List<Event> getEvents(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart,
                          LocalDateTime rangeEnd, int from, int size);
    Event getById(Long id);
}
