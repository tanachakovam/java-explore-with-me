package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {
    private final StatService statService;

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(name = "uris", required = false) List<String> uris,
                                    @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {
        return statService.getStats(start, end, uris, unique);

    }

    @PostMapping("/hit")
    public EndpointHitDto addHit(@RequestBody EndpointHitDto endpointHitDto) {
        return statService.postHit(endpointHitDto);
    }
}
