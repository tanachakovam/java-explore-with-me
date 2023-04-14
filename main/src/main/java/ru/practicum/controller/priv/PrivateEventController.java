
package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.request.UpdateEventUserRequest;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final EventService eventService;


    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsOfUser(@PathVariable Long userId,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getEventsOfUser(userId, PageRequest.of(from / size, size));
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }


    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventOfUser(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        return eventService.getEventOfUser(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventOfUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventOfUser(userId, eventId, updateEventUserRequest);
    }
}

