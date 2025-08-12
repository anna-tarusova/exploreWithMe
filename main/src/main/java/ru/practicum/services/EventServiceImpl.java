package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.entities.Event;
import ru.practicum.entities.enums.State;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.repositories.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public Event saveEvent(Event event) {
        if (event.getId() == null) {
            event.setState(State.WAITING);
        } else {
            Event eventInDB = eventRepository.findById(event.getId())
                    .orElseThrow(() -> new NotFoundException((String.format("An event with id=%d is not found", event.getId()))));
            if (event.getState() == State.PUBLISHED &&
                    eventInDB.getState() != State.WAITING
            ) {
                throw new ConflictException("The event is not in the WAITING state");
            } else if (event.getState() == State.CANCELLED &&
                    eventInDB.getState() == State.PUBLISHED
            ) {
                throw new ConflictException("The event is in the PUBLISHED state");
            }
        }

        return eventRepository.save(event);
    }

    @Override
    public List<Event> getEventsByUserId(Long userId, int from, int size) {
        return eventRepository.findByUserId(userId, from, size);
    }

    @Override
    public List<Event> getEvents(List<Long> users,
                                 List<State> states,
                                 List<Long> categories,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 int from,
                                 int size) {
        return eventRepository.findEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("An event with id = %d does not exist", id)));
    }
}
