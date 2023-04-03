package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.SortParam;

import java.time.LocalDateTime;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e " +
            "where e.initiator.id in ?1 " +
            "and e.state in ?2 " +
            "and e.category.id in ?3 " +
            "and e.eventDate >= ?4 " +
            "and e.eventDate <= ?5 ")
    List<Event> findAllEvents(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Boolean existsByCategoryId(Long id);

    @Query("select e from Event e " +
            "where (upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            "or upper(e.description) like upper(concat('%', ?1, '%'))) " +
            "and e.category.id in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate >= ?4 " +
            "and e.eventDate <= ?5 " +
            "and e.participantLimit - e.confirmedRequests > 0 " +
            "and e.state = ?7 " +
            "group by ?6")
    List<Event> findAllAvailablePublishedEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, SortParam sort, EventState state, Pageable pageable);

    @Query("select e from Event e " +
            "where (upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            "or upper(e.description) like upper(concat('%', ?1, '%'))) " +
            "and e.category.id in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate >= ?4 " +
            "and e.eventDate <= ?5 " +
            "and e.state = ?7 " +
            "group by ?6")
    List<Event> findAllPublishedEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, SortParam sort, EventState state, Pageable pageable);

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    Event findEventByIdAndInitiator_Id(Long id, Long userId);

    Event findEventByIdAndAndState(Long id, EventState state);

    List<Event> findAllByIdIn(List<Long> ids);
}