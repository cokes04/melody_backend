package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.post.TestPostEntityGenerator;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import net.datafaker.Faker;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestMusicEntityGenerator {
    private static final Faker faker = new Faker();

    public static Map<Long, MusicEntity> saveRandomMusicEntitys(TestEntityManager em, UserEntity userEntity, Music.Status status, int count){
        return IntStream.range(0, count)
                .mapToObj(i -> saveRandomMusicEntity(em, status, userEntity))
                .peek(e -> e.setUserEntity(userEntity))
                .collect(Collectors.toMap(
                        e -> e.getId(),
                        e -> e
                ));
    }

    public static MusicEntity saveRandomMusicEntity(TestEntityManager em, Music.Status status, UserEntity savedUserEntity){
        MusicEntity musicEntity = randomMusicEntity(null, status, savedUserEntity);
        em.persist(musicEntity);
        return musicEntity;
    }

    public static MusicEntity randomMusicEntity(){
        return randomMusicEntity(randomStatus(), TestUserEntityGenerator.randomUserEntity());
    }


    public static MusicEntity randomMusicEntity(Music.Status status, UserEntity userEntity){
        return randomMusicEntity(randomId(), status, userEntity);
    }

    public static MusicEntity randomMusicEntity(Long musicId, Music.Status status, UserEntity userEntity){
        return MusicEntity.builder()
                .id(musicId)
                .emotion(randomEmotion())
                .explanation(randomExplanation())
                .imageUrl(randomImageUrl())
                .musicUrl(randomMusicUrl())
                .status(status)
                .userEntity(userEntity)
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

    public static String randomMusicUrl(){
        return TestMusicDomainGenerator.randomMusicUrl().getValue();
    }

    public static Music.Status randomStatus(){
        return TestMusicDomainGenerator.randomStatus();
    }
}
