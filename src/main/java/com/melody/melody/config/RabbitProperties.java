package com.melody.melody.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@Getter
@Component
@ConfigurationProperties(prefix = "rabbit")
public class RabbitProperties {

    private Map<String, Exchange> exchange = new HashMap<>();

    private Map<String, Queue> queue = new HashMap<>();

    private List<Binding> binding = new ArrayList<>();

    @ToString
    @Getter
    @Setter
    public static class Exchange{
        private String name;
        private boolean durable;
        private boolean autoDelete;
    }
    @ToString
    @Getter
    @Setter
    public static class Queue{
        private String name;
        private boolean durable;
        private boolean exclusive;
        private boolean autoDelete;
    }
    @ToString
    @Getter
    @Setter
    public static class Binding{
        private String exchange;
        private String queue;
        private String key;
    }

}
