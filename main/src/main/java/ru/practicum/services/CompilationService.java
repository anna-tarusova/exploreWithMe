package ru.practicum.services;

import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequestDto;
import ru.practicum.entities.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation createCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(Long compId);

    Compilation updateCompilation(Long compId, UpdateCompilationRequestDto request);

    Compilation getCompilation(Long id);

    List<Compilation> getCompilations(Boolean pinned, int from, int size);
}
