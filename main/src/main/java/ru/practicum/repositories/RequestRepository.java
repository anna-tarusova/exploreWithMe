package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM requests r WHERE r.user_id = :id")
    List<Request> findByUserId(@Param("id") Long id);

    @Query(value = "SELECT r FROM Request r WHERE r.event.id = :id")
    List<Request> findByEventId(@Param("id") Long id);


    Optional<Request> findByUserIdAndId(Long userId, Long id);

    @Query(value = "SELECT count(*) FROM Request r " +
            "WHERE r.event.id = :eventId " +
            "and r.id not in (:ids)" +
            "and r.state = RequestState.CONFIRMED")
    int countOfOtherConfirmedRequests(@Param("eventId") Long eventId, @Param("ids") List<Long> ids);

    @Query(value = "SELECT count(*) FROM Request r " +
            "WHERE r.event.id = :eventId " +
            "and r.state = RequestState.CONFIRMED")
    int countOfConfirmedRequests(@Param("eventId") Long eventId);

    @Query(value = "SELECT count(*) FROM Request r " +
            "WHERE r.event.id = :eventId")
    int countOfRequests(@Param("eventId") Long eventId);
}
