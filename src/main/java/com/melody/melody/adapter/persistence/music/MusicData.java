package com.melody.melody.adapter.persistence.music;

import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicData{
    private Long id;

    private Long userId;

    private Emotion emotion;

    private String explanation;

    private String imageUrl;

    private String musicUrl;

    private Music.Status status;

    @Builder
    @QueryProjection
    public MusicData(Long id, Long userId, Emotion emotion, String explanation, String imageUrl, String musicUrl, Music.Status status) {
        this.id = id;
        this.userId = userId;
        this.emotion = emotion;
        this.explanation = explanation;
        this.imageUrl = imageUrl;
        this.musicUrl = musicUrl;
        this.status = status;
    }
}
