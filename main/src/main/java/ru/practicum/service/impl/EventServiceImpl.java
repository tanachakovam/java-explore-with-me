package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.request.UpdateEventAdminRequest;
import ru.practicum.dto.request.UpdateEventUserRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.SortParam;
import ru.practicum.model.enums.StateAction;
import ru.practicum.model.enums.StateActionAdmin;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.EventService;
import ru.practicum.service.EventSpecifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventMapper mapper;
    private final CommentMapper commentMapper;
    private final EventRepository repository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        Collection<Event> events;
        List<EventFullDto> eventsForAdmin = new ArrayList<>();
        List<CommentDto> comments;

        Specification<Event> specification = EventSpecifications.getFilteredEventsForAdmin(users, states, categories, rangeStart, rangeEnd);
        Page<Event> eventPage = repository.findAll(specification, pageable);
        events = eventPage.getContent();

        for (Event event : events) {
            comments = getComments(event);
            eventsForAdmin.add(mapper.toEventFullDto(event, comments));
        }
        return eventsForAdmin;
    }


    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = findEventById(eventId);
        if (event.getPublishedOn() != null) {
            if (event.getPublishedOn().plusHours(1).isBefore(event.getEventDate())) {
                throw new ValidationException("The event does not meet the editing rules.");
            }
        }

        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() -> new NotFoundException("Category is not found"));
            event.setCategory(category);
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT)) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ValidationException("The event was already published.");
                }
                if (event.getState().equals(EventState.CANCELED)) {
                    throw new ValidationException("The event was cancelled.");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                event.setViews(0L);
            } else {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ValidationException("The event was already published and can not be cancelled.");
                }
                event.setState(EventState.CANCELED);
            }
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Wrong event date.");
            }
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        mapper.mapAdminRequestToEvent(updateEventAdminRequest, event);
        repository.save(event);
        return mapper.toEventFullDto(event);
    }


    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublishedEvent(Long id, String ip, String path) {
        saveHit(ip, path);
        Event event = repository.findEventByIdAndAndState(id, EventState.PUBLISHED);
        List<CommentDto> comments = getComments(event);

        event.setViews(event.getViews() + 1);
        return mapper.toEventFullDto(event, comments);
    }

    private List<CommentDto> getComments(Event event) {
        List<Comment> comments = commentRepository.findAllByEvent_Id(event.getId());
        return commentMapper.toCommentDtoCollection(comments);
    }

    private void saveHit(String ip, String path) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setIp(ip);
        endpointHitDto.setUri(path);
        endpointHitDto.setApp("main");
        endpointHitDto.setTimestamp(LocalDateTime.now());
        statsClient.postHit(endpointHitDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, SortParam sort, Pageable pageable, String ip, String path) {
        Collection<Event> events;
        List<EventShortDto> publishedEvents = new ArrayList<>();
        List<CommentDto> comments;
        Specification<Event> specification = EventSpecifications.getFilteredEvents(text, categories, paid, rangeStart, rangeEnd, sort, EventState.PUBLISHED, onlyAvailable);
        Page<Event> eventPage = repository.findAll(specification, pageable);
        events = eventPage.getContent();
        saveHit(ip, path);
        for (Event event : events) {
            event.setViews(event.getViews() + 1);
            comments = getComments(event);
            publishedEvents.add(mapper.toEventShortDto(event, comments));
        }
        return publishedEvents;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsOfUser(Long userId, Pageable pageable) {
        Collection<Event> events = repository.findAllByInitiator_Id(userId, pageable);
        List<EventShortDto> eventsOfUser = new ArrayList<>();
        List<CommentDto> comments;
        for (Event event : events) {
            comments = getComments(event);
            eventsOfUser.add(mapper.toEventShortDto(event, comments));
        }
        return eventsOfUser;
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event event = mapper.toEvent(newEventDto);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));
        event.setInitiator(user);

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Field: eventDate. Error: must contain a date that has not yet occurred..");
        }
        event.setCreatedOn(LocalDateTime.now());

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new NotFoundException("Category is not found"));
        event.setCategory(category);

        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0L);

        Event addedEvent = repository.save(event);
        return mapper.toEventFullDto(addedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventOfUser(Long userId, Long eventId) {
        return mapper.toEventFullDto(repository.findEventByIdAndInitiator_Id(eventId, userId));
    }

    @Override
    @Transactional
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found"));
        Event event = findEventById(eventId);
        if (event.getInitiator().getId().equals(user.getId())) {
            if (updateEventUserRequest == null) {
                return mapper.toEventFullDto(event);
            }
            if (event.getPublishedOn() != null) {
                throw new ValidationException("Only pending or canceled events can be changed.");
            }
            if (updateEventUserRequest.getCategory() != null) {
                Category category = categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() -> new NotFoundException("Category is not found"));
                event.setCategory(category);
            }

            if (updateEventUserRequest.getEventDate() != null) {
                if (event.getEventDate().minusHours(2).isAfter(LocalDateTime.now())) {
                    throw new ValidationException("Only pending or canceled events can be changed.");
                }
                event.setEventDate(updateEventUserRequest.getEventDate());
            }

            if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
            mapper.mapUserRequestToEvent(updateEventUserRequest, event);
            repository.save(event);
        }
        return mapper.toEventFullDto(event);
    }

    @Transactional(readOnly = true)
    public Event findEventById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Event  is not found"));
    }
}

