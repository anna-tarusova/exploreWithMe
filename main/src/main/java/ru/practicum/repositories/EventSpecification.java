package ru.practicum.repositories;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.entities.Event;

import java.time.LocalDateTime;

public class EventSpecification {
    public static Specification<Event> hasEventDateAfter(LocalDateTime rangeStart) {
        return (root, query, criteriaBuilder) -> {
            if (rangeStart == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
        };
    }

    public static Specification<Event> hasEventDateBefore(LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) -> {
            if (rangeEnd == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
        };
    }

    public static Specification<Event> isPaid(Boolean paid) {
        return (root, query, criteriaBuilder) -> {
            if (paid == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("paid"), paid);
        };
    }

    public static Specification<Event> withFetches() {
        return (root, query, criteriaBuilder) -> {
            // Только для query типа SELECT (не COUNT)
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("user", JoinType.INNER);
                root.fetch("category", JoinType.INNER);
            }
            return null;
        };
    }

    public static Specification<Event> filterEvents(
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean paid
    ) {

        return Specification
                .where(withFetches())
                .and(hasEventDateAfter(rangeStart))
                .and(hasEventDateBefore(rangeEnd))
                .and(isPaid(paid));
    }
}
