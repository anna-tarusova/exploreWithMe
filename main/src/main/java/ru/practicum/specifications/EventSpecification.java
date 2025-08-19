package ru.practicum.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.entities.Event;
import ru.practicum.entities.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

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

    public static Specification<Event> states(List<EventState> states) {
        return (root, query, criteriaBuilder) -> {
            if (states == null || states.isEmpty()) {
                return null;
            }
            CriteriaBuilder.In<EventState> inClause = criteriaBuilder.in(root.get("state"));
            for (EventState state : states) {
                inClause.value(state);
            }
            return inClause;
        };
    }

    public static Specification<Event> categories(List<Long> categories) {
        return (root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return null;
            }

            CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(root.get("category").get("id"));
            for (Long id : categories) {
                inClause.value(id);
            }
            return inClause;
        };
    }

    public static Specification<Event> users(List<Long> userIds) {
        return (root, query, criteriaBuilder) -> {
            if (userIds == null || userIds.isEmpty()) {
                return null;
            }

            CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(root.get("user").get("id"));
            for (Long id : userIds) {
                inClause.value(id);
            }
            return inClause;
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

    public static Specification<Event> order() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("eventDate")));
            return null;
        };
    }

    public static Specification<Event> filterEvents(
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean paid,
            List<EventState> states,
            List<Long> categories,
            List<Long> users
    ) {

        return Specification
                .where(withFetches())
                .and(hasEventDateAfter(rangeStart))
                .and(hasEventDateBefore(rangeEnd))
                .and(isPaid(paid))
                .and(states(states))
                .and(categories(categories))
                .and(users(users));
    }
}
