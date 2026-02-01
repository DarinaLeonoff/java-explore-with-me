package ru.practicum.stats.server.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatEntityTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();

        StatEntity entity = new StatEntity(1L, "app-name", "/test", "127.0.0.1", now);

        assertEquals(1L, entity.getId());
        assertEquals("app-name", entity.getApp());
        assertEquals("/test", entity.getUri());
        assertEquals("127.0.0.1", entity.getIp());
        assertEquals(now, entity.getTimestamp());
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        StatEntity entity = new StatEntity();

        entity.setId(2L);
        entity.setApp("service");
        entity.setUri("/stats");
        entity.setIp("192.168.0.1");
        entity.setTimestamp(now);

        assertEquals(2L, entity.getId());
        assertEquals("service", entity.getApp());
        assertEquals("/stats", entity.getUri());
        assertEquals("192.168.0.1", entity.getIp());
        assertEquals(now, entity.getTimestamp());
    }
}
