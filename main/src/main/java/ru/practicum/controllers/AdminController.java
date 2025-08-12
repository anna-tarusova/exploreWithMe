package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventAdminRequestDto;
import ru.practicum.dto.UserDto;
import ru.practicum.entities.Category;
import ru.practicum.entities.Event;
import ru.practicum.entities.User;
import ru.practicum.entities.enums.State;
import ru.practicum.entities.enums.StateAction;
import ru.practicum.mappers.EventMapper;
import ru.practicum.services.CategoriesService;
import ru.practicum.services.EventService;
import ru.practicum.services.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.mappers.CategoryMapper.toEntity;
import static ru.practicum.mappers.CategoryMapper.toDto;
import static ru.practicum.mappers.UserMapper.toEntity;
import static ru.practicum.mappers.UserMapper.toDto;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CategoriesService categoriesService;
    private final UserService userService;
    private final EventService eventService;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Category category = toEntity(categoryDto);
        category = categoriesService.saveCategory(category);
        return new ResponseEntity<>(toDto(category), HttpStatus.OK);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable("catId") Long catId) {
        categoriesService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> partiallyUpdateCategory(@PathVariable("catId") Long catId, @RequestBody CategoryDto categoryDto) {
        Category category = toEntity(categoryDto);
        category = categoriesService.partiallyUpdate(category, catId);
        return new ResponseEntity<>(toDto(category), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        User user = toEntity(userDto);
        user = userService.saveUser(user);
        return new ResponseEntity<>(toDto(user), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return userService.getUsers(ids, from, size);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam String rangeStart,
            @RequestParam String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        LocalDateTime rs = LocalDateTime.parse(rangeStart, df);
        LocalDateTime re = LocalDateTime.parse(rangeEnd, df);

        return eventService.getEvents(users, states, categories, rs, re, from, size)
                .stream().map(EventMapper::toDto).toList();
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> changeEvent(
            @PathVariable("eventId") Long eventId,
            @RequestBody UpdateEventAdminRequestDto request) {
        Event event = eventService.getById(eventId);
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = categoriesService.getCategoryById(request.getCategory());
            event.setCategory(category);
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            if (request.getLocation().getLocationLat() != null) {
                event.setLocationLat(request.getLocation().getLocationLat());
            }
            if (request.getLocation().getLocationLon() != null) {
                event.setLocationLon(request.getLocation().getLocationLon());
            }
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit((request.getParticipantLimit()));
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getState() == StateAction.PUBLISH_EVENT) {
            event.setState(State.PUBLISHED);
        } else if (request.getState() == StateAction.REJECT_EVENT) {
            event.setState(State.CANCELLED);
        }

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        event = eventService.saveEvent(event);
        return new ResponseEntity<>(EventMapper.toDto(event), HttpStatus.OK);
    }
}
