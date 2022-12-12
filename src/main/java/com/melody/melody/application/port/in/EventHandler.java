package com.melody.melody.application.port.in;

public interface EventHandler<T> {
    void handle(T event);
}
