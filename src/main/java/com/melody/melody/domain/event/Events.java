package com.melody.melody.domain.event;

import com.melody.melody.domain.event.Event;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class Events {
    private static ApplicationEventPublisher applicationEventPublisher;

    public Events(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public static void raise(Event event) {
        if (event == null) return;
        applicationEventPublisher.publishEvent(event);
    }
}