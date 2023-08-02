package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatsClient extends BaseClient {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                    .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                    .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                    .build()
        );
    }

    public ResponseEntity<Object> addHit(HitDto hitDto) {
        log.info("stats-client: addHit: {}", hitDto);
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> requestParams = Map.of(
                "start", start.format(DATE_TIME_FORMAT),
                "end", end.format(DATE_TIME_FORMAT),
                "uris", String.join(",", uris),
                "unique", unique
        );
        String request = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        log.info("stats-client: getStats: requestParams: {}", requestParams);
        return get(request, requestParams);
    }
}
