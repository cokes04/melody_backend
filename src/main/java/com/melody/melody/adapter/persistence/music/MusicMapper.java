package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MusicMapper {

    public Music toModel(MusicEntity entity){
        return Music.builder()
                .id(entity.getId() == null ? Identity.empty() : Identity.from(entity.getId()))
                .emotion(entity.getEmotion())
                .status(entity.getStatus())
                .musicUrl(new Music.MusicUrl(entity.getMusicUrl()))
                .imageUrl(new Music.ImageUrl(entity.getImageUrl()))
                .explanation(new Music.Explanation(entity.getExplanation()))
                .userId(new Identity(entity.getUserEntity().getId()))
                .build();
    }

    public Music toModel(MusicData data){
        return Music.builder()
                .id(data.getId() == null ? Identity.empty() : Identity.from(data.getId()))
                .emotion(data.getEmotion())
                .status(data.getStatus())
                .musicUrl(new Music.MusicUrl(data.getMusicUrl()))
                .imageUrl(new Music.ImageUrl(data.getImageUrl()))
                .explanation(new Music.Explanation(data.getExplanation()))
                .userId(new Identity(data.getUserId()))
                .build();
    }

    public MusicEntity toEntity(Music music){
        return MusicEntity.builder()
                .id(music.getId().isEmpty() ? null : music.getId().getValue())
                .emotion(music.getEmotion())
                .explanation(music.getExplanation().getValue())
                .imageUrl(music.getImageUrl().getValue())
                .musicUrl(music.getMusicUrl().isEmpty() ? null : music.getMusicUrl().getValue())
                .status(music.getStatus())
                .createdDate(LocalDateTime.now())
                .userEntity(UserEntity.builder().id(music.getUserId().getValue()).build())
                .build();
    }
}
