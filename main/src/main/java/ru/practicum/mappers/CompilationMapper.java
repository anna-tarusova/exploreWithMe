package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.dto.*;
import ru.practicum.entities.Compilation;

@Component
public class CompilationMapper {
    public static Compilation toEntity(NewCompilationDto compilationDto) {
        if (compilationDto == null) return null;
        Compilation compilation = new Compilation();
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.getPinned());
        compilation.setId(compilation.getId());
        return compilation;
    }

    public static CompilationDto toDto(Compilation compilation) {
        if (compilation == null) return null;
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setEvents(compilation.getEvents().stream().map(EventMapper::toShortDto).toList());
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());
        return dto;
    }
}
