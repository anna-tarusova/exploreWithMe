package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dtos.ViewStatsDto;
import ru.practicum.entities.Hit;
import ru.practicum.repositories.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public void saveHit(Hit hit) {
        repository.save(hit);
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return repository.getViewStatsUnique(start, end, uris);
        } else {
            return repository.getViewStats(start, end, uris);
        }
    }
}
