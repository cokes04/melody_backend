package com.melody.melody.adapter.web.music.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.melody.melody.adapter.web.converter.EmotionToStringConverter;
import com.melody.melody.adapter.web.converter.MusicStatusToStringConverter;
import com.melody.melody.domain.model.Music;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MusicResponse {
    private final Long musicId;

    private final Long userId;

    @JsonSerialize(converter = EmotionToStringConverter.class)
    private final Music.Emotion emotion;

    private final String explanation;

    private final String imageUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String musicUrl;

    @JsonSerialize(converter = MusicStatusToStringConverter.class)
    private final Music.Status status;

    public static MusicResponse to(Music music){
        return MusicResponse.builder()
                .musicId(music.getId().getValue())
                .userId(music.getUserId().getValue())
                .imageUrl(music.getImageUrl().getValue())
                .explanation(music.getExplanation().getValue())
                .emotion(music.getEmotion())
                .musicUrl(music.getMusicUrl().isEmpty() ? null : music.getMusicUrl().getValue())
                .status(music.getStatus())
                .build();
    }
}
