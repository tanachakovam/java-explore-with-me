
package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;

import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.UpdateEventUserRequest;

import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class PrivateController {
    private final EventService eventService;
    private final RequestService requestService;
    private final CommentService commentService;


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

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(@PathVariable Long userId,
                                                           @RequestParam(name = "eventId") String eventId) {
        return requestService.addParticipationRequest(userId, Long.parseLong(eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventOfUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventOfUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return requestService.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        return requestService.changeRequestStatus(userId, eventId, statusUpdateRequest);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }


    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @PostMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addNewComment(@PathVariable Long userId,
                                    @RequestParam(name = "eventId") String eventId,
                                    @RequestBody @Valid NewCommentDto commentDto) {
        return commentService.addNewComment(userId, Long.parseLong(eventId), commentDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentDto updateCommentByUser(@PathVariable Long userId, @PathVariable Long commentId, @RequestBody NewCommentDto commentDto) {
        return commentService.updateCommentByUser(userId, commentId, commentDto);
    }
}

