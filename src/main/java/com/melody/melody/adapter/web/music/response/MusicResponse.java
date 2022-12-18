package com.melody.melody.adapter.web.music.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.melody.melody.adapter.web.converter.EmotionToStringConverter;
import com.melody.melody.adapter.web.converter.MusicStatusToStringConverter;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MusicResponse {
    private final Long musicId;

    private final String userId;

    @JsonSerialize(converter = EmotionToStringConverter.class)
    private final Emotion emotion;

    private final String explanation;

    private final String imageUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String musicUrl;

    @JsonSerialize(converter = MusicStatusToStringConverter.class)
    private final Music.Status status;

    public static MusicResponse to(Music music){
        return MusicResponse.builder()
                .musicId(music.getId().map(Music.MusicId::getValue).orElse(-1L))
                .userId(music.getUserId())
                .imageUrl(music.getImageUrl().getValue())
                .explanation(music.getExplanation().getValue())
                .emotion(music.getEmotion())
                .musicUrl(music.getMusicUrl().map(Music.MusicUrl::getValue).orElse(null))
                .status(music.getStatus())
                .build();
    }
}
