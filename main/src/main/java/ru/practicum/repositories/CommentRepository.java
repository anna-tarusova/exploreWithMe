package ru.practicum.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entities.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.event " +
            "WHERE c.event.id = :id and c.status = CommentStatus.PUBLISHED")
    List<Comment> findByEventId(@Param("id") Long eventId, Pageable pageable);
}
