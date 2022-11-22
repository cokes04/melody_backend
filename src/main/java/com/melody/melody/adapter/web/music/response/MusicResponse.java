package com.melody.melody.adapter.web.music.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MusicResponse {
    private final Long musicId;

    private final Long userId;

    private final String emotion;

    private final String explanation;

    private final String imageUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String musicUrl;

    private final String status;

    public static MusicResponse to(Music music){
        return MusicResponse.builder()
                .musicId(music.getId().map(Music.MusicId::getValue).orElse(-1L))
                .userId(music.getUserId().getValue())
                .imageUrl(music.getImageUrl().getValue())
                .explanation(music.getExplanation().getValue())
                .emotion(music.getEmotion().name().toLowerCase())
                .musicUrl(music.getMusicUrl().map(Music.MusicUrl::getValue).orElse(null))
                .status(music.getStatus().name().toLowerCase())
                .build();
    }
}
