package ru.practicum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.entities.Compilation;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation c " +
            "LEFT JOIN FETCH c.events " +
            "WHERE (:pinned IS NULL OR c.pinned = :pinned)")
    Page<Compilation> getCompilations(@Param("pinned") Boolean pinned, Pageable pageable);
}
