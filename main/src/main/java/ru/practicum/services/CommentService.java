package ru.practicum.services;

import ru.practicum.dto.ModerationCommentDto;
import ru.practicum.entities.Comment;

import java.util.List;

public interface CommentService {
    Comment saveComment(Comment comment);

    Comment getById(Long id);

    List<Comment> getComments(Long eventId, int from, int size);

    Comment moderateComment(Long commentId, ModerationCommentDto moderationCommentDto);
}
