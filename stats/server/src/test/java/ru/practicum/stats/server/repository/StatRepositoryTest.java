package ru.practicum.stats.server.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.server.model.StatEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StatRepositoryTest {
    @Autowired
    private StatRepository repository;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
    }

    @Test
    void saveTest() {
        StatEntity entity = generateEntity();
        StatEntity saved = repository.save(generateEntity());

        assertEquals(entity.getApp(), saved.getApp());
        assertEquals(entity.getUri(), saved.getUri());
        assertEquals(entity.getIp(), saved.getIp());
        assertEquals(entity.getTimestamp(), saved.getTimestamp());
        assertNotNull(saved.getId());
    }

    @Test
    void getByDatesTest() {
        StatEntity entity1 = generateEntity();
        StatEntity saved1 = repository.save(entity1);

        StatEntity entity2 = generateEntity();
        entity2.setTimestamp(LocalDateTime.now().minusDays(3));
        StatEntity saved2 = repository.save(entity2);

        StatEntity entity3 = generateEntity();
        entity3.setTimestamp(LocalDateTime.now().plusDays(2));
        StatEntity saved3 = repository.save(entity3);

        List<StatEntity> entities = repository.findAllByTimestampBetween(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        StatEntity getEntity = entities.getFirst();

        assertEquals(2, entities.size());

        assertEquals(saved1.getApp(), getEntity.getApp());
        assertEquals(saved1.getUri(), getEntity.getUri());
        assertEquals(saved1.getIp(), getEntity.getIp());
        assertEquals(saved1.getTimestamp(), getEntity.getTimestamp());
        assertEquals(saved1.getId(), getEntity.getId());
    }

    @Test
    void getByDatesAndUrisTest() {
        StatEntity entity1 = generateEntity();
        StatEntity saved1 = repository.save(entity1);

        StatEntity entity2 = generateEntity();
        entity2.setUri("/hit");
        StatEntity saved2 = repository.save(entity2);

        StatEntity entity3 = generateEntity();
        entity3.setUri("/hit/2");
        StatEntity saved3 = repository.save(entity3);

        List<StatEntity> entities = repository.findAllByUriInAndTimestampBetween(List.of("/hit"), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2));

        StatEntity getEntity = entities.getFirst();

        assertEquals(2, entities.size());

        assertEquals(saved1.getApp(), getEntity.getApp());
        assertEquals(saved1.getUri(), getEntity.getUri());
        assertEquals(saved1.getIp(), getEntity.getIp());
        assertEquals(saved1.getTimestamp(), getEntity.getTimestamp());
        assertEquals(saved1.getId(), getEntity.getId());
    }

    @Test
    void getUniqueByDatesTest() {
        StatEntity entity1 = generateEntity();
        StatEntity saved1 = repository.save(entity1);

        StatEntity entity2 = generateEntity();
        StatEntity saved2 = repository.save(entity2);

        StatEntity entity3 = generateEntity();
        entity3.setIp("1.1.0.1");
        StatEntity saved3 = repository.save(entity3);

        List<StatEntity> entities = repository.findAllByTimestampBetweenAndIpIsUnique(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2));

        StatEntity getEntity = entities.getFirst();

        assertEquals(2, entities.size());

        assertEquals(saved1.getApp(), getEntity.getApp());
        assertEquals(saved1.getUri(), getEntity.getUri());
        assertEquals(saved1.getIp(), getEntity.getIp());
        assertEquals(saved1.getTimestamp(), getEntity.getTimestamp());
        assertEquals(saved1.getId(), getEntity.getId());
    }

    private StatEntity generateEntity() {
        return StatEntity.builder().app("ewm-main").uri("/hit").ip("1.1.1.1").timestamp(now).build();
    }

}
