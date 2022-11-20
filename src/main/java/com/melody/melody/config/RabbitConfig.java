package com.melody.melody.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Configuration
public class RabbitConfig {
    private final RabbitProperties properties;

    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Declarables exchanges(){
        List<TopicExchange> exchanges = properties.getExchange().values().stream()
                .map(e -> new TopicExchange(e.getName(), e.isDurable(), e.isAutoDelete()))
                .collect(Collectors.toList());

        return new Declarables(exchanges);
    }

    @Bean
    public Declarables queues(){
        List<Queue> queues = properties.getQueue().values().stream()
                .map(q -> new Queue(q.getName(), q.isDurable(), q.isExclusive(), q.isAutoDelete()))
                .collect(Collectors.toList());

        return new Declarables(queues);
    }

    @Bean
    public Declarables bindings(){
        List<Binding> bindings = properties.getBinding().stream()
                .map(b -> new Binding(b.getQueue(), Binding.DestinationType.QUEUE, b.getExchange(), b.getKey(), null))
                .collect(Collectors.toList());

        return new Declarables(bindings);
    }
}
