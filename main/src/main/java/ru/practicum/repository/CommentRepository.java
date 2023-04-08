package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;

import java.time.LocalDateTime;
import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(" select c from Comment c " +
            "where (upper(c.text) like upper(concat('%', ?1, '%'))) " +
            "and c.created >= ?2 " +
            "and c.created <= ?3 ")
    List<Comment> search(String text, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    List<Comment> findAllByEvent_Id(Long eventId);

}
