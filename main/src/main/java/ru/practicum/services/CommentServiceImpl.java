package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ModerationCommentDto;
import ru.practicum.dto.enums.CommentStatusAction;
import ru.practicum.entities.Comment;
import ru.practicum.entities.Event;
import ru.practicum.entities.enums.CommentStatus;
import ru.practicum.entities.enums.EventState;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.repositories.CommentRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Comment saveComment(Comment comment) {
        Event event = comment.getEvent();
        if (Objects.equals(event.getUser().getId(), comment.getUser().getId())) {
            throw new ForbiddenException("You aren't allowed to post a comment to your own event");
        }

        if (Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new ForbiddenException("You aren't allowed to post a comment to not published event");
        }

        comment.setStatus(CommentStatus.PENDING);

        if (comment.getId() == null) {
            return commentRepository.save(comment);
        }

        Comment commentInDb = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id = %d not found", comment.getId())));

        if (!Objects.equals(commentInDb.getUser().getId(), comment.getUser().getId())) {
            throw new ForbiddenException("You aren't allowed to update not your own comment");
        }

        if (commentInDb.getStatus() == CommentStatus.REJECTED) {
            throw new ForbiddenException("You aren't allowed to change a comment in the REJECTED state");
        }

        comment.setCreated(commentInDb.getCreated());
        return commentRepository.save(comment);
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id = %d not found", id)));
    }

    @Override
    public List<Comment> getComments(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return commentRepository.findByEventId(eventId, pageable);
    }

    @Override
    @Transactional
    public Comment moderateComment(Long commentId, ModerationCommentDto moderationCommentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id = %d not found", commentId)));

        if (moderationCommentDto.getStatus() == CommentStatusAction.PUBLISH) {
            comment.setStatus(CommentStatus.PUBLISHED);
        } else if (moderationCommentDto.getStatus() == CommentStatusAction.REJECT) {
            comment.setStatus(CommentStatus.REJECTED);
        }

        return commentRepository.save(comment);
    }
}
