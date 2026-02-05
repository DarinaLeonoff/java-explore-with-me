package ru.practicum.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.client.StatsClientImpl;

@Configuration
public class StatsClientConfig {
    @Bean
    public StatsClient statsClient(
            @Value("${stats.client.url}") String url
    ) {
        return new StatsClientImpl(new RestTemplate(), url);
    }
}
