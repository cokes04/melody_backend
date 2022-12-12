package com.melody.melody.adapter.persistence.user;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.domain.model.*;
import net.datafaker.Faker;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityManager;

public class TestUserEntityGenerator {
    private static final Faker faker = new Faker();

    public static UserEntity saveRandomUserEntity(EntityManager em){
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity();
        userEntity.setId(null);
        em.persist(userEntity);
        return userEntity;
    }

    public static UserEntity saveRandomUserEntity(TestEntityManager em){
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity();
        userEntity.setId(null);
        em.persist(userEntity);
        return userEntity;
    }

    public static UserEntity randomUserEntity(){
        return UserEntity.builder()
                .id(randomId())
                .nickName(randomLastName())
                .email(randomEmail())
                .password(randomPassword())
                .build();
    }

    public static Long randomId(){
        return TestUserDomainGenerator.randomUserId().getValue();
    }

    public static String randomLastName(){
        return TestUserDomainGenerator.randomNickName().getValue();
    }

    public static String randomEmail(){
        return TestUserDomainGenerator.randomEmail().getValue();
    }

    public static String randomPassword(){
        return TestUserDomainGenerator.randomPassword().getEncryptedString();
    }
}
