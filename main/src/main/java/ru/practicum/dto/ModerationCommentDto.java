package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.enums.CommentStatusAction;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModerationCommentDto {
    CommentStatusAction status;
}
