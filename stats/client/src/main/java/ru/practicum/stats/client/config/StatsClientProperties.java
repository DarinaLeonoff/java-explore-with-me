package ru.practicum.stats.client.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class StatsClientProperties {

    private String url;

}
