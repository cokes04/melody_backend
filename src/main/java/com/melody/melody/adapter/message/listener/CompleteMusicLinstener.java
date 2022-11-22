package com.melody.melody.adapter.message.listener;

import com.melody.melody.adapter.message.event.MusicComposed;
import com.melody.melody.application.service.music.CompleteGenerationMusicService;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CompleteMusicLinstener {
    private final CompleteGenerationMusicService service;

    @RabbitListener(id = "complete-music", queues = "${rabbit.queue.complete-music.name}")
    public void receive(MusicComposed musicComposed) {

        CompleteGenerationMusicService.Command command = new CompleteGenerationMusicService.Command(
                new Music.MusicId(musicComposed.getMusicId()),
                new Music.MusicUrl(musicComposed.getMusicUrl())
        );

        service.execute(command);
    }
}
