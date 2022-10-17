package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.response.MusicResponse;
import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.Music;
import org.springframework.stereotype.Component;

@Component
public class GenerateMusicResultMapper{

    public MusicResponse to(GenerateMusicService.Result result){
        Music music = result.getMusic();
        return MusicResponse.builder()
                .musicId(music.getId().orElse(new Music.MusicId(-1L)).getValue())
                .imageUrl(music.getImageUrl().getValue())
                .explanation(music.getExplanation().getValue())
                .emotion(music.getEmotion().name().toLowerCase())
                .status(music.getStatus().name().toLowerCase())
                .build();
    }

}
