package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsServerClient;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.UpdateEventUserRequestDto;
import ru.practicum.dtos.ViewStatsDto;
import ru.practicum.entities.Category;
import ru.practicum.entities.Event;
import ru.practicum.entities.enums.SortEvents;
import ru.practicum.entities.enums.EventState;
import ru.practicum.entities.enums.UpdateStateAction;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.repositories.CategoriesRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.specifications.EventSpecification;
import ru.practicum.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final StatsServerClient statsServerClient;
    private final EventRepository eventRepository;
    private final CategoriesRepository categoriesRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public Event saveEvent(Event event) {
        if (event.getId() == null) {
            event.setState(EventState.PENDING);
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

        Sort sortEvents = Sort.by(Sort.Direction.ASC, "id");

        Specification<Event> spec = EventSpecification.filterEvents(rangeStart, rangeEnd, null, states, categories, users);
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sortEvents);
        return eventRepository.findAll(spec, pageable).stream().toList();
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text,
                                               List<Long> users,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               SortEvents sort,
                                               int from,
                                               int size) {
        Sort sortEvents = Sort.by("id").ascending();

        Specification<Event> spec = EventSpecification.filterEvents(
                rangeStart, rangeEnd, paid, null, categories, users);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sortEvents);
        List<Event> events = eventRepository.findAll(spec, pageable).stream().toList();

        HashMap<String, Event> eventsMap = new HashMap<>();
        events.forEach(e -> eventsMap.put("/events/" + e.getId(), e));

        if (rangeStart == null) {
            rangeStart = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(9999, 12, 31, 0, 0, 0);
        }

        List<ViewStatsDto> views = statsServerClient.stats(rangeStart, rangeEnd, eventsMap.keySet().stream().toList(), true);
        HashMap<String, Long> viewsMap = new HashMap<>();
        views.forEach(view -> viewsMap.put(view.getUri(), view.getHits()));

        if (sort == SortEvents.VIEWS) {
            views.sort((a, b) -> Math.toIntExact(b.getHits() - a.getHits()));
            List<Event> newOrder = new ArrayList<>();
            views.forEach(view -> {
                newOrder.add(eventsMap.get(view.getUri()));
                eventsMap.remove(view.getUri());
            });
            newOrder.addAll(eventsMap.values());
            events = newOrder;
        }

        List<EventShortDto> result = events.stream().map(EventMapper::toShortDto).toList();
        result.forEach(eventShortDto -> {
            String uri = "/events/" + eventShortDto.getId();
            if (viewsMap.containsKey(uri)) {
                eventShortDto.setViews(viewsMap.get(uri));
            }
        });
        return result;
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("The event with id = %d does not exist", id)));
    }

    @Override
    @Transactional
    public Event updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto dto) {
        try {
            Event eventInDb = eventRepository.findByUserIdAndId(userId, eventId)
                    .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d of user with" +
                            " id = %d is not found", eventId, userId)));

            if (eventInDb.getState() == EventState.PUBLISHED) {
                throw new ConflictException("It's not allowed to change published event");
            }

            if (dto.getTitle() != null) {
                eventInDb.setTitle(dto.getTitle());
            }

            if (dto.getStateAction() != null) {
                if (eventInDb.getState() == EventState.PENDING && dto.getStateAction() != UpdateStateAction.CANCEL_REVIEW) {
                    throw new ConflictException("Published event can be only cancelled");
                } else if (eventInDb.getState() == EventState.CANCELED && dto.getStateAction() != UpdateStateAction.SEND_TO_REVIEW) {
                    throw new ConflictException("Cancelled event can be only published");
                }

                if (dto.getStateAction() == UpdateStateAction.SEND_TO_REVIEW) {
                    eventInDb.setState(EventState.PENDING);
                } else {
                    eventInDb.setState(EventState.CANCELED);
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

    @Override
    public int countConfirmedRequests(Long eventId) {
        return requestRepository.countOfConfirmedRequests(eventId);
    }
}
