package ru.practicum.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatsServerClient;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dtos.HitDto;
import ru.practicum.dtos.ViewStatsDto;
import ru.practicum.entities.Category;
import ru.practicum.entities.Compilation;
import ru.practicum.entities.Event;
import ru.practicum.entities.enums.EventState;
import ru.practicum.entities.enums.Sort;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CategoryMapper;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.mappers.EventMapper;
import ru.practicum.services.CategoriesService;
import ru.practicum.services.CompilationService;
import ru.practicum.services.EventService;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicController extends BaseController {

    private final EventService eventService;
    private final CompilationService compilationService;
    private final CategoriesService categoriesService;

    private final StatsServerClient statsServerClient;

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) Sort sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (categories != null && categories.stream().anyMatch(c -> c <= 0))
        {
            throw new BadRequestException("Bad category");
        }

        LocalDateTime rs = rangeStart == null ? null : LocalDateTime.parse(rangeStart, df);
        LocalDateTime re = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, df);
        List<EventShortDto> eventShortDtos = eventService.getEventsPublic(text, users, categories, paid, rs, re, onlyAvailable, sort, from, size);
        return new ResponseEntity<>(eventShortDtos, HttpStatus.OK);
    }

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        List<Compilation> compilations = compilationService.getCompilations(pinned, from, size);
        List<CompilationDto> compilationDtos = compilations.stream().map(CompilationMapper::toDto).toList();
        return new ResponseEntity<>(compilationDtos, HttpStatus.OK);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(
            @PathVariable("compId") Long id) {
        Compilation compilation = compilationService.getCompilation(id);
        return new ResponseEntity<>(CompilationMapper.toDto(compilation), HttpStatus.OK);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable("id") Long id, HttpServletRequest request) {
        Event event = eventService.getById(id);
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(String.format("The event with id = %d is not published", id));
        }

        List<String> uris = new ArrayList<>();
        uris.add(request.getRequestURI());
        List<ViewStatsDto> viewStatsDtos = statsServerClient.stats(event.getEventDate().minusDays(1), LocalDateTime.now().plusDays(1), uris, true);

        EventFullDto eventFullDto = EventMapper.toDto(event);
        eventFullDto.setViews(viewStatsDtos.size());

        HitDto hit = new HitDto();
        hit.setIp(request.getRemoteAddr());
        hit.setUri(request.getRequestURI());
        hit.setApp("ewm");
        hit.setTimestamp(LocalDateTime.now());
        statsServerClient.hit(hit);

        return new ResponseEntity<>(eventFullDto, HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        List<Category> categories = categoriesService.getCategories(from, size);
        return new ResponseEntity<>(categories.stream().map(CategoryMapper::toDto).toList(), HttpStatus.OK);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable("catId") Long catId) {
        Category category = categoriesService.getCategoryById(catId);
        return new ResponseEntity<>(CategoryMapper.toDto(category), HttpStatus.OK);
    }
}
