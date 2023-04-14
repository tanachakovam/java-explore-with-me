package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final CommentMapper mapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto addNewComment(Long userId, Long eventId, NewCommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Comment can not be blank.");
        }
        Event event = eventRepository.findEventByIdAndAndState(eventId, EventState.PUBLISHED);
        if (event == null) {
            throw new NotFoundException("Event is not found");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found. Only registered user can add comment."));
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        comment.setEvent(event);
        comment.setAuthor(user);
        comment.setText(commentDto.getText());
        repository.save(comment);
        return mapper.toCommentDto(comment);
    }


    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = repository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment is not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Only author of the comment can delete it.");
        }
        repository.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByUser(Long userId, Long commentId, NewCommentDto commentDto) {
        Comment comment = repository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment is not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Only author of the comment can change it.");
        }

        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Comment can not be blank.");
        }
        comment.setCreated(LocalDateTime.now());
        comment.setText(commentDto.getText());
        repository.save(comment);
        return mapper.toCommentDto(comment);
    }



    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> search(String text, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Collection<Comment> comments = repository.search(text, rangeStart, rangeEnd, pageable);
        return mapper.toCommentDtoCollection(comments);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        repository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment is not found"));
        repository.deleteById(commentId);
    }
}
