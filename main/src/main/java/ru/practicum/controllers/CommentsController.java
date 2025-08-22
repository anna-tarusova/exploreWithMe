package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ModerationCommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.dto.enums.CommentStatusAction;
import ru.practicum.entities.Comment;
import ru.practicum.entities.Event;
import ru.practicum.entities.User;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.mappers.CommentMapper;
import ru.practicum.services.CommentService;
import ru.practicum.services.EventService;
import ru.practicum.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.mappers.CommentMapper.toDto;
import static ru.practicum.mappers.CommentMapper.toEntity;

@RestController
@RequiredArgsConstructor
public class CommentsController extends BaseController {

    private final CommentService commentService;
    private final UserService userService;
    private final EventService eventService;

    @PostMapping("/users/{userId}/events/{eventId}/comment")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid NewCommentDto commentDto) {
        Comment comment = toEntity(commentDto);
        comment = saveOrUpdate(userId, eventId, comment);
        return new ResponseEntity<>(toDto(comment), HttpStatus.CREATED);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/comment")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid UpdateCommentDto commentDto) {
        Comment comment = toEntity(commentDto);
        comment.setId(commentDto.getId());
        comment.setUpdated(LocalDateTime.now());
        comment = saveOrUpdate(userId, eventId, comment);
        return new ResponseEntity<>(toDto(comment), HttpStatus.OK);
    }

    @GetMapping("/events/{eventId}/comments")
    public ResponseEntity<List<CommentDto>> updateComment(
            @PathVariable("eventId") Long eventId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        List<Comment> comments = commentService.getComments(eventId, from, size);
        return new ResponseEntity<>(comments.stream().map(CommentMapper::toDto).toList(), HttpStatus.OK);
    }

    @PatchMapping("/admin/comments/{commentId}")
    public ResponseEntity<CommentDto> moderateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody ModerationCommentDto moderationCommentDto) {
        Comment comment = commentService.moderateComment(commentId, moderationCommentDto);
        return new ResponseEntity<>(CommentMapper.toDto(comment), HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}/cancel")
    public ResponseEntity<CommentDto> cancel(
            @PathVariable("userId") Long userId,
            @PathVariable("commentId") Long commentId) {
        Comment commentInDb = commentService.getById(commentId);

        if (!Objects.equals(commentInDb.getUser().getId(), userId)) {
            throw new ForbiddenException("You aren't allowed to change other people's comments");
        }

        Comment comment = commentService.moderateComment(commentId, new ModerationCommentDto(CommentStatusAction.REJECT));
        return new ResponseEntity<>(CommentMapper.toDto(comment), HttpStatus.OK);
    }

    private Comment saveOrUpdate(Long userId, Long eventId, Comment comment) {
        User user = userService.getUser(userId);
        Event event = eventService.getById(eventId);
        comment.setUser(user);
        comment.setEvent(event);
        return commentService.saveComment(comment);
    }
}
