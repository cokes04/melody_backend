package com.melody.melody.adapter.web.music.response;

import com.melody.melody.domain.model.Music;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MusicResponse {
    private final Long musicId;

    private final String emotion;

    private final String explanation;

    private final String imageUrl;

    private final String status;

    public static MusicResponse to(Music music){
        return MusicResponse.builder()
                .musicId(music.getId().orElse(new Music.MusicId(-1L)).getValue())
                .imageUrl(music.getImageUrl().getValue())
                .explanation(music.getExplanation().getValue())
                .emotion(music.getEmotion().name().toLowerCase())
                .status(music.getStatus().name().toLowerCase())
                .build();
    }
}
