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
        return saveRandomMusicEntitys(em, userEntity, status, count, 0);
    }


    public static Map<Long, MusicEntity> saveRandomMusicEntitys(TestEntityManager em, UserEntity userEntity, Music.Status status, int count, int plusMinut){
        LocalDateTime now = LocalDateTime.now().withNano(0);
        return IntStream.range(0, count)
                .mapToObj(i -> saveRandomMusicEntity(em, status, now.plusMinutes(i * plusMinut), userEntity))
                .collect(Collectors.toMap(
                        e -> e.getId(),
                        e -> e
                ));
    }

    public static MusicEntity saveRandomMusicEntity(TestEntityManager em, Music.Status status, UserEntity savedUserEntity){
        return saveRandomMusicEntity(em, status, LocalDateTime.now().withNano(0), savedUserEntity);
    }

    public static MusicEntity saveRandomMusicEntity(TestEntityManager em, Music.Status status, LocalDateTime createdDate, UserEntity savedUserEntity){
        MusicEntity musicEntity = randomMusicEntity(null, status, createdDate.withNano(0), savedUserEntity);
        em.persist(musicEntity);
        return musicEntity;
    }

    public static MusicEntity randomMusicEntity(){
        return randomMusicEntity(randomStatus(), TestUserEntityGenerator.randomUserEntity());
    }


    public static MusicEntity randomMusicEntity(Music.Status status, UserEntity userEntity){
        LocalDateTime createdDate = LocalDateTime.now().withNano(0);
        return randomMusicEntity(randomId(), status, createdDate, userEntity);
    }

    public static MusicEntity randomMusicEntity(Long musicId, Music.Status status, LocalDateTime createdDate, UserEntity userEntity){
        return MusicEntity.builder()
                .id(musicId)
                .emotion(randomEmotion())
                .explanation(randomExplanation())
                .imageUrl(randomImageUrl())
                .musicUrl(randomMusicUrl())
                .status(status)
                .createdDate(createdDate)
                .userId(userEntity.getId())
                .build();
    }

    public static MusicData randomMusicData(){
        return MusicData.builder()
                .id(randomId())
                .emotion(randomEmotion())
                .explanation(randomExplanation())
                .imageUrl(randomImageUrl())
                .musicUrl(randomMusicUrl())
                .status(randomStatus())
                .userId(TestUserEntityGenerator.randomId())
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
