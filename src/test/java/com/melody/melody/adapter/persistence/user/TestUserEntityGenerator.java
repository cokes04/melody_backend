package com.melody.melody.adapter.persistence.user;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.domain.model.*;
import net.datafaker.Faker;

public class TestUserEntityGenerator {
    private static final Faker faker = new Faker();

    public static UserEntity randomUserEntity(){
        return UserEntity.builder()
                .id(randomId())
                .lastName(randomLastName())
                .firstName(randomFirstName())
                .email(randomEmail())
                .password(randomPassword())
                .build();
    }

    public static Long randomId(){
        return TestUserDomainGenerator.randomUserId().getValue();
    }

    public static String randomLastName(){
        return TestUserDomainGenerator.randomLastName();
    }

    public static String randomFirstName(){
        return TestUserDomainGenerator.randomFirstName();
    }

    public static String randomEmail(){
        return TestUserDomainGenerator.randomEmail();
    }

    public static String randomPassword(){
        return TestUserDomainGenerator.randomPassword().getEncryptedString();
    }
}
