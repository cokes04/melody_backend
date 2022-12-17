package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MusicMapper {

    public Music toModel(MusicEntity entity){
        return Music.builder()
                .id(new Music.MusicId(entity.getId()))
                .emotion(entity.getEmotion())
                .status(entity.getStatus())
                .musicUrl(new Music.MusicUrl(entity.getMusicUrl()))
                .imageUrl(new Music.ImageUrl(entity.getImageUrl()))
                .explanation(new Music.Explanation(entity.getExplanation()))
                .userId(new User.UserId(entity.getUserId()))
                .build();
    }

    public Music toModel(MusicData data){
        return Music.builder()
                .id(new Music.MusicId(data.getId()))
                .emotion(data.getEmotion())
                .status(data.getStatus())
                .musicUrl(new Music.MusicUrl(data.getMusicUrl()))
                .imageUrl(new Music.ImageUrl(data.getImageUrl()))
                .explanation(new Music.Explanation(data.getExplanation()))
                .userId(new User.UserId(data.getUserId()))
                .build();
    }

    public MusicEntity toEntity(Music music){
        return MusicEntity.builder()
                .id(music.getId().map(Music.MusicId::getValue).orElse(null))
                .emotion(music.getEmotion())
                .explanation(music.getExplanation().getValue())
                .imageUrl(music.getImageUrl().getValue())
                .musicUrl(music.getMusicUrl().map(Music.MusicUrl::getValue).orElse(null))
                .status(music.getStatus())
                .createdDate(LocalDateTime.now())
                .userId(music.getUserId().getValue())
                .build();
    }
}
