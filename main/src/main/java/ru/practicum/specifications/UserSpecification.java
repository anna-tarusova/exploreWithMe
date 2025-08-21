package ru.practicum.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.entities.User;

import java.util.List;

public class UserSpecification {
    public static Specification<User> userIn(List<Long> ids) {
        return (root, query, criteriaBuilder) -> {
            if (ids == null || ids.isEmpty()) {
                return null;
            }
            CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(root.get("id"));
            for (Long id : ids) {
                inClause.value(id);
            }
            return inClause;
        };
    }

    public static Specification<User> filterUsers(
            List<Long> ids
    ) {
        return Specification.where(userIn(ids));
    }
}
