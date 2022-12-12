package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.music.TestMusicEntityGenerator;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import net.datafaker.Faker;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestPostEntityGenerator {
    private static final Faker faker = new Faker();

   public static Map<Long, PostEntity> saveRandomPostEntitys(TestEntityManager em, UserEntity userEntity, boolean open, boolean deleted, int count) {
        return saveRandomPostEntitys(em, userEntity, open, deleted, count, 0);
    }

    public static Map<Long, PostEntity> saveRandomPostEntitys(TestEntityManager em, UserEntity userEntity, boolean open, boolean deleted, int count, int plusMinute){
        LocalDateTime now = LocalDateTime.now().withNano(0);
        return IntStream.range(0, count)
                .mapToObj(i -> TestPostEntityGenerator.saveRandomPostEntity(em, open, deleted, now.plusMinutes(plusMinute * i), userEntity))
                .collect(Collectors.toMap(
                        e -> e.getId(),
                        e -> e
                ));
    }

    public static PostEntity saveRandomPostEntity(TestEntityManager em, boolean open, boolean deleted){
        return saveRandomPostEntity(em, open, deleted, LocalDateTime.now());
    }

    public static PostEntity saveRandomPostEntity(TestEntityManager em, boolean open, boolean deleted, LocalDateTime createdDate){
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        MusicEntity musicEntity = TestMusicEntityGenerator.saveRandomMusicEntity(em, deleted ? Music.Status.COMPLETION : Music.Status.DELETED, userEntity);
        return TestPostEntityGenerator.saveRandomPostEntity(em, open, deleted, createdDate, userEntity, musicEntity);
    }

    public static PostEntity saveRandomPostEntity(TestEntityManager em, boolean open, boolean deleted, LocalDateTime createdDate,
                                                  UserEntity savedUserEntity){
        MusicEntity musicEntity = TestMusicEntityGenerator.saveRandomMusicEntity(em, deleted ? Music.Status.COMPLETION : Music.Status.DELETED, savedUserEntity);
        PostEntity postEntity = randomPostEntity(null, open, deleted, createdDate, savedUserEntity, musicEntity);
        em.persist(postEntity);
        return postEntity;
    }

    public static PostEntity saveRandomPostEntity(TestEntityManager em, boolean open, boolean deleted, LocalDateTime createdDate,
                                                  UserEntity savedUserEntity, MusicEntity savedMusicEntity){
       PostEntity postEntity = randomPostEntity(null, open, deleted, createdDate.withNano(0), savedUserEntity, savedMusicEntity);
       em.persist(postEntity);
       return postEntity;
    }

    public static PostEntity randomPostEntity(boolean open, boolean deleted){
       LocalDateTime now = LocalDateTime.now().withNano(0);
       return randomPostEntity(
               randomId(),
               open,
               deleted,
               now,
               TestUserEntityGenerator.randomUserEntity(),
               TestMusicEntityGenerator.randomMusicEntity()
       );
    }

    public static PostEntity randomPostEntity(Long id, boolean open, boolean deleted, LocalDateTime createdDate,
                                              UserEntity userEntity, MusicEntity musicEntity){
        return PostEntity.builder()
                .id(id)
                .title(randomTitle())
                .content(randomContent())
                .open(open)
                .deleted(deleted)
                .musicEntity(musicEntity)
                .userEntity(userEntity)
                .createdDate(createdDate)
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
