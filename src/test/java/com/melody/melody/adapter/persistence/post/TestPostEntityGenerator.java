package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.music.TestMusicEntityGenerator;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import net.datafaker.Faker;

public class TestPostEntityGenerator {
    private static final Faker faker = new Faker();

    public static PostEntity randomPostEntity(){
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity();
        MusicEntity musicEntity = TestMusicEntityGenerator.randomMusicEntity();
        musicEntity.setUserEntity(userEntity);

        return PostEntity.builder()
                .id(randomId())
                .title(randomTitle())
                .content(randomContent())
                .open(true)
                .deleted(false)
                .musicEntity(musicEntity)
                .userEntity(userEntity)
                .build();
    }

    public static long randomId(){
        return TestPostDomainGenerator.randomPostId().getValue();
    }
    public static String randomTitle(){
        return TestPostDomainGenerator.randomTitle().getValue();
    }
    public static String randomContent(){
        return TestPostDomainGenerator.randomContent().getValue();
    }
}
