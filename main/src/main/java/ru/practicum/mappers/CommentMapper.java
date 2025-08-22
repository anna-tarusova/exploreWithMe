package ru.practicum.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.entities.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        if (comment == null) return null;
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setAuthor(UserMapper.toShortDto(comment.getUser()));
        dto.setEvent(EventMapper.toShortDto(comment.getEvent()));
        dto.setCommentText(comment.getCommentText());
        dto.setCreated(comment.getCreated());
        dto.setUpdated(comment.getUpdated());
        dto.setStatus(comment.getStatus());
        return dto;
    }

    public static Comment toEntity(NewCommentDto dto) {
        if (dto == null) return null;
        Comment comment = new Comment();
        comment.setCommentText(dto.getCommentText());
        return comment;
    }

    public static Comment toEntity(UpdateCommentDto dto) {
        if (dto == null) return null;
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setCommentText(dto.getCommentText());
        return comment;
    }
}
