package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestEventDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.entities.Category;
import ru.practicum.entities.Event;
import ru.practicum.entities.User;
import ru.practicum.mappers.EventMapper;
import ru.practicum.services.CategoriesService;
import ru.practicum.services.EventService;
import ru.practicum.services.UserService;

import java.util.List;

import static ru.practicum.mappers.EventMapper.toDto;
import static ru.practicum.mappers.EventMapper.toEntity;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateController {

    private final CategoriesService categoriesService;
    private final UserService userService;
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> createEvent(@PathVariable("userId") Long userId,
                                                    @Valid @RequestBody RequestEventDto requestEventDto) {
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

        List<Event> events = eventService.getEventsByUserId(userId, from, size);
        List<EventShortDto> eventDtos = events.stream().map(EventMapper::toShortDto).toList();
        return new ResponseEntity<>(eventDtos, HttpStatus.OK);
    }
}
