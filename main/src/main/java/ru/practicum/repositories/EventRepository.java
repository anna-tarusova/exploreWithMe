package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.entities.Event;
import ru.practicum.entities.enums.State;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM events e " +
           // "JOIN categories c ON (e.category_id = c.id) " +
            //"JOIN users u ON (e.user_id = u.id) " +
            "WHERE e.user_id = :id OFFSET :ofs LIMIT :lim")
    List<Event> findByUserId(@Param("id") Long id, @Param("ofs") int ofs, @Param("lim") int lim);

    @Query(nativeQuery = true, value = "SELECT * FROM events e " +
            "WHERE e.user_id IN (:ids) " +
            " AND e.state IN (:states) " +
            " AND e.category_id IN (:categories) " +
            " AND e.event_date >= :range_start" +
            " AND e.event_date <= :range_end" +
            " OFFSET :ofs LIMIT :lim")
    List<Event> findEvents(@Param("ids") List<Long> id,
                             @Param("states") List<State> states,
                             @Param("categories") List<Long> categories,
                             @Param("range_start") LocalDateTime rangeStart,
                             @Param("range_end") LocalDateTime rangeEnd,
                             @Param("ofs") int ofs,
                             @Param("lim") int lim);
}
