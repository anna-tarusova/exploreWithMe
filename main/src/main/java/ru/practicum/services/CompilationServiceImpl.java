package ru.practicum.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequestDto;
import ru.practicum.entities.Compilation;
import ru.practicum.entities.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.repositories.CompilationRepository;
import ru.practicum.repositories.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private CompilationRepository compilationRepository;
    private EventRepository eventRepository;

    @Transactional(readOnly = false)
    public Compilation createCompilation(NewCompilationDto newCompilationDto) {
        try {
            Compilation compilation = CompilationMapper.toEntity(newCompilationDto);
            if (newCompilationDto.getEvents() != null) {
                Set<Event> events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
                compilation.setEvents(events);
            }
            return compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Событие не может быть добавлено более одного раза в подборку");
        }
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public Compilation updateCompilation(Long compId, UpdateCompilationRequestDto request) {
        try {
            Compilation compilation = compilationRepository.findById(compId)
                    .orElseThrow(() -> new NotFoundException(String.format("Compilation with id = %d is not found", compId)));
            if (request.getTitle() != null) {
                compilation.setTitle(request.getTitle());
            }
            if (request.getPinned() != null) {
                compilation.setPinned(request.getPinned());
            }
            if (request.getEvents() != null) {
                Set<Event> events = new HashSet(eventRepository.findAllById(request.getEvents()));
                compilation.setEvents(events);
            }
            return compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Событие не может быть добавлено более одного раза в подборку");
        }
    }

    @Override
    public Compilation getCompilation(Long id) {
        List<Event> events = eventRepository.eventsOfCompilation(id);
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compilation not found"));
        compilation.setEvents(new HashSet<>(events));
        return compilation;
    }

    @Override
    public List<Compilation> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return compilationRepository.getCompilations(pinned, pageable).toList();
    }
}
