package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.server.model.StatEntity;

@Repository
public interface StatRepository extends JpaRepository<StatEntity, Long> {
}
