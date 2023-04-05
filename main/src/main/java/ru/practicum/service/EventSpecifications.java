package ru.practicum.service;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.model.Event;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.SortParam;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventSpecifications {
    public static Specification<Event> getFilteredEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, SortParam sort, EventState state, Boolean onlyAvailable) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (paid != null) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), paid));
            }
            if (text != null) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.upper(root.get("annotation")), "%" + text.toUpperCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), "%" + text.toUpperCase() + "%")));//todo
            }
            if (rangeStart != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }
            if (rangeEnd != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }
            if (categories != null) {
                predicates.add(root.get("category").get("id").in(categories));
            }
            if (sort != null && sort.equals(SortParam.EVENT_DATE)) {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("eventDate")));
            }
            if (sort != null && sort.equals(SortParam.VIEWS)) {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("views")));
            }
            if (onlyAvailable != null && onlyAvailable) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("participantLimit"), 0),
                        criteriaBuilder.greaterThan(root.get("participantLimit"), root.get("confirmedRequests"))));
            }
            predicates.add(criteriaBuilder.equal(root.get("state"), state));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Event> getFilteredEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (users != null) {
                predicates.add(root.get("initiator").get("id").in(users));
            }
            if (states != null) {
                predicates.add(root.get("state").in(states));
            }
            if (rangeStart != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }
            if (categories != null) {
                predicates.add(root.get("category").get("id").in(categories));
            }
            if (rangeEnd != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}