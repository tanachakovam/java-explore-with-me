package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.repository.EventRepository;
import ru.practicum.model.enums.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.repository.RequestRepository;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.RequestService;
import ru.practicum.model.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;


    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        if (requestRepository.findByEventAndRequester(eventId, userId) != null) {
            throw new ValidationException("Request already exists.");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event  is not found"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Initiator of the event can't be participant.");
        }

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ValidationException("Participation in unpublished event is impossible.");
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ValidationException("Participation in event is impossible. Participation limit exceeded.");
        }

        List<Request> requestsForEvent = requestRepository.findAllByEvent(eventId);
        if (!event.getRequestModeration() && requestsForEvent.size() >= event.getParticipantLimit()) {
            throw new ValidationException("Participation in event is impossible. Participation limit exceeded.");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Field: eventDate. Error: Participation in event is impossible.");
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(eventId);
        request.setRequester(userId);
        request.setStatus(EventState.PENDING);
        requestRepository.save(request);
        return requestMapper.toRequestDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        if (eventRepository.findEventByIdAndInitiator_Id(eventId, userId) != null) {
            return requestMapper.toRequestDto(requestRepository.findAllByEvent(eventId));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestMapper.toRequestDto(requestRepository.findAllByRequester(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequester(requestId, userId);
        request.setStatus(EventState.CANCELED);
        requestRepository.save(request);
        return requestMapper.toRequestDto(request);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId);
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return eventRequestStatusUpdateResult;
        }

        if ((event.getConfirmedRequests()) >= event.getParticipantLimit()) {
            throw new ValidationException("Participation limit exceeded.");
        }
        List<ParticipationRequestDto> participationRequestDtos = requestMapper.toRequestDto(requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds()));

        if (RequestStatus.CONFIRMED.equals(eventRequestStatusUpdateRequest.getStatus())) {
            for (ParticipationRequestDto participationRequestDto : participationRequestDtos) {
                participationRequestDto.setStatus("CONFIRMED");
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                eventRequestStatusUpdateResult.setConfirmedRequests(List.of(participationRequestDto));
            }
        } else {
            for (ParticipationRequestDto participationRequestDto : participationRequestDtos) {
                participationRequestDto.setStatus("REJECTED");
            }
            eventRequestStatusUpdateResult.setRejectedRequests(participationRequestDtos);
        }
        return eventRequestStatusUpdateResult;
    }
}

