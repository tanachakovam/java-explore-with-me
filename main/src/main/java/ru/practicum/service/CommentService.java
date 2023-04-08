package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto addNewComment(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto updateCommentByUser(Long userId, Long commentId, NewCommentDto commentDto);

    void deleteCommentByUser(Long userId, Long commentId);

    void deleteComment(Long commentId);

    List<CommentDto> search(String text, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
}
