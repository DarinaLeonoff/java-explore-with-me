package ru.practicum.stats.client;

public interface StatsClient {
    void hit(String app, String uri, String ip);
}
