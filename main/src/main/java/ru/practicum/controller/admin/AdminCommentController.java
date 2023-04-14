package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Validated
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/search")
    public List<CommentDto> search(@RequestParam(value = "text", required = false) String text,
                                   @RequestParam(name = "rangeStart") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                   @RequestParam(name = "rangeEnd") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return commentService.search(text, rangeStart, rangeEnd, PageRequest.of(from / size, size));
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
