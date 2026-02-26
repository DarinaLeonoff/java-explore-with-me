package ru.practicum.ewm.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long id);

    List<Comment> findAllByUserId(Long id);

    List<Comment> findAllByEventIdAndUserId(Long eventId, Long userId);
}
