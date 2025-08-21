package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.entities.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query(nativeQuery = true, value = "SELECT * FROM events e " +
            "WHERE e.user_id = :id OFFSET :ofs LIMIT :lim")
    List<Event> findByUserId(@Param("id") Long id, @Param("ofs") int ofs, @Param("lim") int lim);

    Optional<Event> findByUserIdAndId(Long userId, Long eventId);

    @Query(nativeQuery = true,
            value = "SELECT True WHERE EXISTS (SELECT 1 FROM Events e WHERE e.category_id = :id)" +
                    "UNION ALL " +
                    "SELECT False WHERE NOT EXISTS (SELECT 1 FROM Events e WHERE e.category_id = :id)")
    boolean doEventsExists(@Param("id") Long categoryId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM events e JOIN compilation_events ce ON (ce.event_id = e.id) " +
                    "WHERE ce.compilation_id = :id")
    List<Event> eventsOfCompilation(@Param("id") Long compilationId);
}
