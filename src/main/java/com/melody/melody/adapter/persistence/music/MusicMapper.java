package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class MusicMapper {

    Music toModel(MusicEntity entity){
        return Music.builder()
                .id(new Music.MusicId(entity.getId()))
                .emotion(entity.getEmotion())
                .status(entity.getStatus())
                .musicUrl(new Music.MusicUrl(entity.getMusicUrl()))
                .imageUrl(new Music.ImageUrl(entity.getImageUrl()))
                .explanation(new Music.Explanation(entity.getExplanation()))
                .userId(new User.UserId(entity.getUserEntity().getId()))
                .build();
    }

    MusicEntity toEntity(Music music){
        return MusicEntity.builder()
                .id(music.getId().map(Music.MusicId::getValue).orElse(null))
                .emotion(music.getEmotion())
                .explanation(music.getExplanation().getValue())
                .imageUrl(music.getImageUrl().getValue())
                .musicUrl(music.getMusicUrl().map(Music.MusicUrl::getValue).orElse(null))
                .status(music.getStatus())
                .userEntity(UserEntity.builder().id(music.getUserId().getValue()).build())
                .build();
    }
}
