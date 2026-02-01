package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.server.model.StatEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<StatEntity, Long> {
    List<StatEntity> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
                SELECT s
                FROM StatEntity s
                WHERE s.timestamp BETWEEN :start AND :end
                  AND s.id = (
                      SELECT MIN(s2.id)
                      FROM StatEntity s2
                      WHERE s2.ip = s.ip
                        AND s2.timestamp BETWEEN :start AND :end
                  )
            """)
    List<StatEntity> findAllByTimestampBetweenAndIpIsUnique(LocalDateTime start, LocalDateTime end);

    List<StatEntity> findAllByUriInAndTimestampBetween(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("""
                SELECT s
                    FROM StatEntity s
                    WHERE s.timestamp BETWEEN :start AND :end
                      AND s.uri IN :uris
                      AND s.timestamp = (
                          SELECT MIN(s2.timestamp)
                          FROM StatEntity s2
                          WHERE s2.ip = s.ip
                            AND s2.uri IN :uris
                            AND s2.timestamp BETWEEN :start AND :end
                      )
            """)
    List<StatEntity> findAllUniqueIpByUrisBetween(List<String> uris, LocalDateTime start, LocalDateTime end);

}
