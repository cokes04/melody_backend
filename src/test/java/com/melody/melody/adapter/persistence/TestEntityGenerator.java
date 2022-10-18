package com.melody.melody.adapter.persistence;

import com.melody.melody.adapter.persistence.entity.MusicEntity;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestDomainGenerator;
import net.datafaker.Faker;

public class TestEntityGenerator {
    private static final Faker faker = new Faker();

    public static MusicEntity randomMusicEntity(){
        return MusicEntity.builder()
                .id(randomId())
                .emotion(randomEmotion())
                .explanation(randomExplanation())
                .imageUrl(randomImageUrl())
                .status(randomStatus())
                .build();
    }

    public static Long randomId(){
        return TestDomainGenerator.randomMusicId().getValue();
    }

    public static Emotion randomEmotion(){
        return TestDomainGenerator.randomEmotion();
    }

    public static String randomExplanation(){
        return TestDomainGenerator.randomExplanation().getValue();
    }

    public static String randomImageUrl(){
        return TestDomainGenerator.randomImageUrl().getValue();
    }

    public static Music.Status randomStatus(){
        return TestDomainGenerator.randomStatus();
    }
}
