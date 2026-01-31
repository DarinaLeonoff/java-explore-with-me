package ru.practicum.stats.client.config;


import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.StatsClient;

import static org.assertj.core.api.Assertions.assertThat;


class StatsClientAutoConfigurationTest {

    @Test
    void contextLoads() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StatsClientAutoConfiguration.class);

        assertThat(context.getBean(RestTemplate.class)).isNotNull();
        assertThat(context.getBean(StatsClient.class)).isNotNull();
    }
}