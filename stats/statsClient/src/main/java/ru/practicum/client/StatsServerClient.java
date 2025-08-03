package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dtos.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsServerClient extends BaseClient {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    public StatsServerClient(@Value("${stats.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> hit(HitDto hit) {
        return post("/hit", hit);
    }

    public ResponseEntity<Object> stats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        StringBuilder urlBuilder = new StringBuilder("/stats?");
        urlBuilder.append("start=").append(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("&");
        urlBuilder.append("end=").append(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("&");
        if (unique != null) {
            urlBuilder.append("unique=").append(unique);
        }

        // Добавляем несколько uri параметров
        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                urlBuilder.append("&uris=").append(uri);
            }
        }

        return get(urlBuilder.toString());
    }
}
