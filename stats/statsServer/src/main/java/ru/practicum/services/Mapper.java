package ru.practicum.services;

import ru.practicum.dtos.HitDto;
import ru.practicum.entities.Hit;

public class Mapper {
    public static Hit toEntity(HitDto dto) {
        Hit entity = new Hit();
        entity.setIp(dto.getIp());
        entity.setApp(dto.getApp());
        entity.setTimestamp(dto.getTimestamp());
        entity.setUri(dto.getUri());
        return entity;
    }

    public static HitDto toDto(Hit entity) {
        return new HitDto(entity.getApp(), entity.getUri(), entity.getIp(), entity.getTimestamp());
    }
}
