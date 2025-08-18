package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.entities.Event;
import ru.practicum.entities.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query(nativeQuery = true, value = "SELECT * FROM events e " +
            "WHERE e.user_id = :id OFFSET :ofs LIMIT :lim")
    List<Event> findByUserId(@Param("id") Long id, @Param("ofs") int ofs, @Param("lim") int lim);

    Optional<Event> findByUserIdAndId(Long userId, Long eventId);

    @Query(nativeQuery = true, value = "SELECT * FROM events e " +
            "WHERE e.user_id IN (:ids) " +
            " AND e.state IN (:states) " +
            " AND e.category_id IN (:categories) " +
            " AND e.event_date >= :range_start" +
            " AND e.event_date <= :range_end" +
            " OFFSET :ofs LIMIT :lim")
    List<Event> findEvents(@Param("ids") List<Long> id,
                           @Param("states") List<EventState> states,
                           @Param("categories") List<Long> categories,
                           @Param("range_start") LocalDateTime rangeStart,
                           @Param("range_end") LocalDateTime rangeEnd,
                           @Param("ofs") int ofs,
                           @Param("lim") int lim);
}
