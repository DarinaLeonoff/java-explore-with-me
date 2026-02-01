package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.server.model.StatEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<StatEntity, Long> {
    List<StatEntity> findAllByCreatedBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
                SELECT s
                FROM StatEntity s
                WHERE s.created BETWEEN :start AND :end
                  AND s.id = (
                      SELECT MIN(s2.id)
                      FROM StatEntity s2
                      WHERE s2.ip = s.ip
                        AND s2.created BETWEEN :start AND :end
                  )
            """)
    List<StatEntity> findAllByCreatedBetweenAndIpIsUnique(LocalDateTime start, LocalDateTime end);

    List<StatEntity> findAllByUriInAndCreatedBetween(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("""
                SELECT s
                    FROM StatEntity s
                    WHERE s.created BETWEEN :start AND :end
                      AND s.uri IN :uris
                      AND s.created = (
                          SELECT MIN(s2.created)
                          FROM StatEntity s2
                          WHERE s2.ip = s.ip
                            AND s2.uri IN :uris
                            AND s2.created BETWEEN :start AND :end
                      )
            """)
    List<StatEntity> findAllUniqueIpByUrisBetween(List<String> uris, LocalDateTime start, LocalDateTime end);

}
