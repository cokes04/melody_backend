package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import net.datafaker.Faker;

public class TestMusicEntityGenerator {
    private static final Faker faker = new Faker();

    public static MusicEntity randomMusicEntity(){
        return MusicEntity.builder()
                .id(randomId())
                .emotion(randomEmotion())
                .explanation(randomExplanation())
                .imageUrl(randomImageUrl())
                .status(randomStatus())
                .userEntity(TestUserEntityGenerator.randomUserEntity())
                .build();
    }

    public static Long randomId(){
        return TestMusicDomainGenerator.randomMusicId().getValue();
    }

    public static Emotion randomEmotion(){
        return TestMusicDomainGenerator.randomEmotion();
    }

    public static String randomExplanation(){
        return TestMusicDomainGenerator.randomExplanation().getValue();
    }

    public static String randomImageUrl(){
        return TestMusicDomainGenerator.randomImageUrl().getValue();
    }

    public static Music.Status randomStatus(){
        return TestMusicDomainGenerator.randomStatus();
    }
}
