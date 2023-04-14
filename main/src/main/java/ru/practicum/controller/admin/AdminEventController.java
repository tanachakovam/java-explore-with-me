package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.UpdateEventAdminRequest;
import ru.practicum.model.enums.EventState;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                        @RequestParam(name = "states", required = false) List<EventState> states,
                                        @RequestParam(name = "categories", required = false) List<Long> categories,
                                        @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, PageRequest.of(from / size, size));
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEvent(eventId, updateEventAdminRequest);
    }
}
