
package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

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

