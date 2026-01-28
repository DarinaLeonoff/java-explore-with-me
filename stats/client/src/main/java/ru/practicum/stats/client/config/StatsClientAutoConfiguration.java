package ru.practicum.stats.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.client.StatsClientImpl;

@Configuration
public class StatsClientAutoConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StatsClient statsClient(
            RestTemplate restTemplate,
            StatsClientProperties properties
    ) {
        return new StatsClientImpl(restTemplate, properties.getUrl());
    }
}
