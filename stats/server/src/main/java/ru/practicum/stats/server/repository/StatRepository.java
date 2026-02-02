package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.stats.server.model.StatEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<StatEntity, Long> {
    @Query("""
            select new ru.practicum.dto.StatsResponseDto(
                s.app,
                s.uri,
                count(distinct s.ip)
            )
            from StatEntity s
            where s.timestamp between :start and :end
            and (:uris is null or s.uri in :uris)
            group by s.app, s.uri
            """)
    List<StatsResponseDto> findStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            select new ru.practicum.dto.StatsResponseDto(
                s.app,
                s.uri,
                count(s.id)
            )
            from StatEntity s
            where s.timestamp between :start and :end
            and (:uris is null or s.uri in :uris)
            group by s.app, s.uri
            """)
    List<StatsResponseDto> findStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
