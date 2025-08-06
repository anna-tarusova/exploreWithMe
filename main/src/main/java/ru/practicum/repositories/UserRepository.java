package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT * FROM users u WHERE u.id IN :ids OFFSET :ofs LIMIT :lim")
    List<User> findByIds(@Param("ids") List<Long> ids, @Param("ofs") int from, @Param("lim") int size);
}
