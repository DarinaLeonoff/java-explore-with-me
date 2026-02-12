package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("""
                SELECT e FROM Event e
                WHERE e.initiator.id = :id
            """)
    Page<Event> findAllByInitiatorId(long id, Pageable pageable);

    Event findByIdAndInitiatorId(long eventId, long userId);

    Page<Event> findByState(EventState state, Pageable pageable);
}
