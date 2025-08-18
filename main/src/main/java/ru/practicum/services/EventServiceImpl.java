package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.dto.UpdateEventUserRequestDto;
import ru.practicum.entities.Category;
import ru.practicum.entities.Event;
import ru.practicum.entities.enums.Sort;
import ru.practicum.entities.enums.EventState;
import ru.practicum.entities.enums.UpdateStateAction;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.repositories.CategoriesRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.EventSpecification;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoriesRepository categoriesRepository;

    @Override
    public Event saveEvent(Event event) {
        if (event.getId() == null) {
            event.setState(EventState.WAITING);
        } else {
            Event eventInDB = eventRepository.findById(event.getId())
                    .orElseThrow(() -> new NotFoundException((String.format("An event with id=%d is not found", event.getId()))));
            if (event.getState() == EventState.PUBLISHED &&
                    eventInDB.getState() != EventState.WAITING
            ) {
                throw new ConflictException("The event is not in the WAITING state");
            } else if (event.getState() == EventState.CANCELLED &&
                    eventInDB.getState() == EventState.PUBLISHED
            ) {
                throw new ConflictException("The event is in the PUBLISHED state");
            }
        }

        return eventRepository.save(event);
    }

    @Override
    public Event getEventByUserIdAndId(Long userId, Long eventId) {
        return eventRepository.findByUserIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d of user with id = %d", userId, eventId)));
    }

    @Override
    public List<Event> getEventsByUserId(Long userId, int from, int size) {
        return eventRepository.findByUserId(userId, from, size);
    }

    @Override
    public List<Event> getEvents(List<Long> users,
                                 List<EventState> states,
                                 List<Long> categories,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 int from,
                                 int size) {
        return eventRepository.findEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @Override
    public List<Event> getEventsPublic(String text,
                                       List<Long> categories,
                                       Boolean paid,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Boolean onlyAvailable,
                                       Sort sort,
                                       int from,
                                       int size) {
        Specification<Event> spec = EventSpecification.filterEvents(rangeStart, rangeEnd, paid);
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findAll(spec, pageable).stream().toList();
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("An event with id = %d does not exist", id)));
    }

    @Override
    public Event updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto dto) {
        try {
            Event eventInDb = eventRepository.findByUserIdAndId(userId, eventId)
                    .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d of user with" +
                            " id = %d is not found", eventId, userId)));
            if (dto.getTitle() != null) {
                eventInDb.setTitle(dto.getTitle());
            }
            if (dto.getState() != null) {
                if (eventInDb.getState() == EventState.WAITING && dto.getState() != UpdateStateAction.CANCEL_REVIEW) {
                    throw new ConflictException("Published event can be only cancelled");
                } else if (eventInDb.getState() == EventState.CANCELLED && dto.getState() != UpdateStateAction.SEND_TO_REVIEW) {
                    throw new ConflictException("Cancelled event can be only published");
                } else if (eventInDb.getState() == EventState.PUBLISHED) {
                    throw new ConflictException("It's not allowed to change publish");
                }
                if (dto.getState() == UpdateStateAction.SEND_TO_REVIEW) {
                    eventInDb.setState(EventState.WAITING);
                } else {
                    eventInDb.setState(EventState.CANCELLED);
                }
            }
            if (dto.getPaid() != null) {
                eventInDb.setPaid(dto.getPaid());
            }
            if (dto.getRequestModeration() != null) {
                eventInDb.setRequestModeration(dto.getRequestModeration());
            }
            if (dto.getEventDate() != null) {
                eventInDb.setEventDate(dto.getEventDate());
            }
            if (dto.getAnnotation() != null) {
                eventInDb.setAnnotation(dto.getAnnotation());
            }
            if (dto.getLocation() != null) {
                if (dto.getLocation().getLocationLon() != null) {
                    eventInDb.setLocationLon(dto.getLocation().getLocationLon());
                }
                if (dto.getLocation().getLocationLat() != null) {
                    eventInDb.setLocationLat(dto.getLocation().getLocationLat());
                }
            }
            if (dto.getCategory() != null) {
                Category category = categoriesRepository.findById(dto.getCategory())
                        .orElseThrow(() -> new NotFoundException(String.format("Category with id = %d is not found",
                                dto.getCategory())));
                eventInDb.setCategory(category);
            }
            if (dto.getParticipantLimit() != null) {
                eventInDb.setParticipantLimit(dto.getParticipantLimit());
            }
            if (dto.getEventDate() != null) {
                eventInDb.setEventDate(dto.getEventDate());
            }
            return eventRepository.save(eventInDb);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Событие не может быть добавлено более одного раза в подборку");
        }
    }
}
