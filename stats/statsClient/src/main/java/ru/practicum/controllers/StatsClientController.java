package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsServerClient;
import ru.practicum.dtos.HitDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsClientController {
    private final StatsServerClient client;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveHit(@RequestBody HitDto hit) {
        log.info("hit, hit = {}", hit.toString());
        return client.hit(hit);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> stats(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime end,

            @RequestParam(required = false)
            List<String> uris,

            @RequestParam(defaultValue = "false")
            Boolean unique
    ) {
        log.info("stats, start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        return client.stats(start, end, uris, unique);
    }
}
