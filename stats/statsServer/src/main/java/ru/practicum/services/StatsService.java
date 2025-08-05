package ru.practicum.services;

import ru.practicum.dtos.ViewStatsDto;
import ru.practicum.entities.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveHit(Hit hit);

    List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
