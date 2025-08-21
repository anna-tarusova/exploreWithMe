package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.entities.Category;
import ru.practicum.entities.Compilation;
import ru.practicum.entities.Event;
import ru.practicum.entities.User;
import ru.practicum.entities.enums.EventState;
import ru.practicum.entities.enums.CreateStateAction;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.mappers.EventMapper;
import ru.practicum.services.CategoriesService;
import ru.practicum.services.CompilationService;
import ru.practicum.services.EventService;
import ru.practicum.services.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.mappers.CategoryMapper.toEntity;
import static ru.practicum.mappers.CategoryMapper.toDto;
import static ru.practicum.mappers.UserMapper.toEntity;
import static ru.practicum.mappers.UserMapper.toDto;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController extends BaseController {

    private final CategoriesService categoriesService;
    private final UserService userService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        Category category = toEntity(categoryDto);
        category = categoriesService.saveCategory(category);
        return new ResponseEntity<>(toDto(category), HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable("catId") Long catId) {
        categoriesService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> partiallyUpdateCategory(@PathVariable("catId") Long catId, @RequestBody @Valid CategoryDto categoryDto) {
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
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        LocalDateTime rs = rangeStart == null ? null : LocalDateTime.parse(rangeStart, df);
        LocalDateTime re = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, df);

        List<EventFullDto> events = eventService.getEvents(users, states, categories, rs, re, from, size)
                        .stream().map(EventMapper::toDto).toList();

        events.forEach(event -> event.setConfirmedRequests(eventService.countConfirmedRequests(event.getId())));

        return events;
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> changeEvent(
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid UpdateEventAdminRequestDto request) {
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

        if (request.getStateAction() == CreateStateAction.PUBLISH_EVENT) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("The event is not in the PENDING state");
            }
            event.setState(EventState.PUBLISHED);
        } else if (request.getStateAction() == CreateStateAction.REJECT_EVENT) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("The event is in the PUBLISHED state");
            }
            event.setState(EventState.CANCELED);
        }

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        event = eventService.saveEvent(event);
        return new ResponseEntity<>(EventMapper.toDto(event), HttpStatus.OK);
    }

    @PostMapping("/compilations")
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        Compilation compilation = compilationService.createCompilation(compilationDto);
        return new ResponseEntity<>(CompilationMapper.toDto(compilation), HttpStatus.CREATED);
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> deleteCompilation(@PathVariable("compId") Long compId) {
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable("compId") Long compId,
                                                            @Valid @RequestBody UpdateCompilationRequestDto request) {
        Compilation compilation = compilationService.updateCompilation(compId, request);
        return new ResponseEntity<>(CompilationMapper.toDto(compilation), HttpStatus.OK);
    }
}
