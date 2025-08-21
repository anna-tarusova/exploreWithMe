package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.entities.Category;
import ru.practicum.entities.Event;
import ru.practicum.entities.Request;
import ru.practicum.entities.User;
import ru.practicum.entities.enums.RequestStateAction;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.services.CategoriesService;
import ru.practicum.services.EventService;
import ru.practicum.services.RequestService;
import ru.practicum.services.UserService;

import java.util.List;
import java.util.Set;

import static ru.practicum.mappers.EventMapper.toDto;
import static ru.practicum.mappers.EventMapper.toEntity;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateController {

    private final CategoriesService categoriesService;
    private final UserService userService;
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> createEvent(@PathVariable("userId") Long userId,
                                                    @Valid @RequestBody NewEventDto requestEventDto) {
        Category category = categoriesService.getCategoryById(requestEventDto.getCategory());
        User user = userService.getUser(userId);

        Event event = toEntity(requestEventDto);
        event.setCategory(category);
        event.setUser(user);

        event = eventService.saveEvent(event);
        EventFullDto responseEventDto = toDto(event);

        return new ResponseEntity<>(responseEventDto, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getEvents(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {

        Set<Event> events = eventService.getEventsByUserId(userId, from, size);
        List<EventShortDto> eventDtos = events.stream().map(EventMapper::toShortDto).toList();
        return new ResponseEntity<>(eventDtos, HttpStatus.OK);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> sendRequest(
            @PathVariable("userId") Long userId,
            @RequestParam("eventId") Long eventId
    ) {
        User user = userService.getUser(userId);
        Event event = eventService.getById(eventId);

        Request request = new Request();
        request.setEvent(event);
        request.setUser(user);
        request = requestService.saveRequest(request);

        return new ResponseEntity<>(RequestMapper.toDto(request), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable("userId") Long userId) {
        List<Request> requests = requestService.getRequestsByUserId(userId);
        List<ParticipationRequestDto> requestDtos = requests.stream().map(RequestMapper::toDto).toList();
        return new ResponseEntity<>(requestDtos, HttpStatus.OK);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable("userId") Long userId,
                                                                 @PathVariable("requestId") Long requestId) {
        Request request = requestService.getRequestById(userId, requestId);
        Request requestAfterUpdate = requestService.cancel(request);
        return new ResponseEntity<>(RequestMapper.toDto(requestAfterUpdate), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable("userId") Long userId,
                                                    @PathVariable("eventId") Long eventId,
                                                    @Valid @RequestBody UpdateEventUserRequestDto dto) {
        Event event = eventService.updateEvent(userId, eventId, dto);
        return new ResponseEntity<>(EventMapper.toDto(event), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable("userId") Long userId,
                                                 @PathVariable("eventId") Long eventId) {
        Event event = eventService.getEventByUserIdAndId(userId, eventId);
        return new ResponseEntity<>(EventMapper.toDto(event), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByUserAndEvent(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        List<Request> requests = requestService.getRequestsByUserIdAndEventId(eventId, userId);
        return new ResponseEntity<>(requests.stream().map(RequestMapper::toDto).toList(), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResultDto> updateRequestsByUserAndEvent(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody(required = false) EventRequestStatusUpdateRequestDto request) {
        List<List<Request>> requests = requestService.updateRequests(userId,
                eventId, request == null ? List.of() : request.getRequestIds(), request == null ? RequestStateAction.CONFIRMED : request.getStatus());
        EventRequestStatusUpdateResultDto result = new EventRequestStatusUpdateResultDto();
        result.setConfirmedRequests(requests.get(0).stream().map(RequestMapper::toDto).toList());
        result.setRejectedRequests(requests.get(1).stream().map(RequestMapper::toDto).toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

