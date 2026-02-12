package ru.practicum.ewm.event.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventSpecification {
    public static Specification<Event> withPublicFilters(String text, List<Integer> categories, Boolean paid,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            //filter by text
            if (text != null && !text.isBlank()) {

                String pattern = "%" + text.toLowerCase() + "%";

                Predicate annotationLike = cb.like(cb.lower(root.get("annotation")), pattern);

                Predicate descriptionLike = cb.like(cb.lower(root.get("description")), pattern);

                predicates.add(cb.or(annotationLike, descriptionLike));
            }

            //filter by categories
            addCategoryFilter(categories, root, predicates);

            //filter by paid
            if (paid != null) {
                predicates.add(cb.equal(root.get("paid"), paid));
            }

            //filter by dates
            addDatesFilter(rangeStart, rangeEnd, root, cb, predicates);

            //filter available only
            if (Boolean.TRUE.equals(onlyAvailable)) {

                Predicate noLimit = cb.equal(root.get("participantLimit"), 0);

                Predicate hasSpots = cb.lessThan(root.get("confirmedRequests"), root.get("participantLimit"));

                predicates.add(cb.or(noLimit, hasSpots));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Event> withAdminFilters(List<Long> users, List<String> states, List<Integer> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            //filter by users
            if (users != null && !users.isEmpty()) {
                predicates.add(root.get("initiator").get("id").in(users));
            }

            //filter by states
            if (states == null || states.isEmpty()) {
                return null;
            }

            //filter by categories
            addCategoryFilter(categories, root, predicates);

            //filter by dates
            addDatesFilter(rangeStart, rangeEnd, root, cb, predicates);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addCategoryFilter(List<Integer> categories, Root<Event> root, List<Predicate> predicates) {
        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categories));
        }
    }

    private static void addDatesFilter(LocalDateTime start, LocalDateTime end, Root<Event> root, CriteriaBuilder cb,
            List<Predicate> predicates) {
        if (start != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), start));
        }

        if (end != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), end));
        }
    }

}
