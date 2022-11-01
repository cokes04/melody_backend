package com.melody.melody.adapter.persistence;

import com.melody.melody.adapter.persistence.entity.MusicEntity;
import com.melody.melody.domain.model.Music;
import org.springframework.stereotype.Component;

@Component
public class MusicMapper {

    Music toModel(MusicEntity entity){
        return Music.builder()
                .id(new Music.MusicId(entity.getId()))
                .emotion(entity.getEmotion())
                .status(entity.getStatus())
                .imageUrl(new Music.ImageUrl(entity.getImageUrl()))
                .explanation(new Music.Explanation(entity.getExplanation()))
                .build();
    }

    MusicEntity toEntity(Music music){
        return MusicEntity.builder()
                .id(music.getId().map(Music.MusicId::getValue).orElse(null))
                .emotion(music.getEmotion())
                .explanation(music.getExplanation().getValue())
                .imageUrl(music.getImageUrl().getValue())
                .status(music.getStatus())
                .build();
    }
}