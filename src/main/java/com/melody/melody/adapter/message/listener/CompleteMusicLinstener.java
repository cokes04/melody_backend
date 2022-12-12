package com.melody.melody.adapter.message.listener;

import com.melody.melody.domain.event.MusicComposed;
import com.melody.melody.application.handler.MusicComposedEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CompleteMusicLinstener {
    private final MusicComposedEventHandler handler;

    @RabbitListener(id = "complete-music", queues = "${rabbit.queue.complete-music.name}")
    public void receive(MusicComposed musicComposed) {
        handler.handle(musicComposed);
    }
}
