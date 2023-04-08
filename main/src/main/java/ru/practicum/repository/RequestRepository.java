package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEvent(Long eventId);

    Request findByEventAndRequester(Long eventId, Long id);

    Request findByIdAndRequester(Long requestId, Long userId);

    List<Request> findAllByRequester(Long id);
}