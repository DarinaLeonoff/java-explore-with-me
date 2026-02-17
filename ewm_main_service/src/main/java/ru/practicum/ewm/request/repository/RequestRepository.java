package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestState;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByUserId(long userId);

    List<Request> findAllByEventId(long eventId);

    long countByEventIdAndStatusIn(Long eventId, List<RequestState> states);

    boolean existsByUserIdAndEventId(long userId, long eventId);
}
