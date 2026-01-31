package ru.practicum.stats.client.config;


import org.springframework.stereotype.Component;

@Component
public class StatsClientProperties {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
