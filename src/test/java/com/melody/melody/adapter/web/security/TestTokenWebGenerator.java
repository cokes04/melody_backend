package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.model.Identity;
import net.datafaker.Faker;

import java.time.LocalDateTime;

public class TestTokenWebGenerator {
    private static final Faker faker = new Faker();

    public static Token randomToken(Identity userId){
        return new Token(userId, randomRefreshToken(), randomAccessToken(), nowLastUpdatedDate());
    }

    public static Token randomToken(){
        return randomToken(randomUserId());
    }

    public static Identity randomUserId(){
        return Identity.from(faker.number().numberBetween(1L, Long.MAX_VALUE));
    }

    public static String randomRefreshToken(){
        return faker.book().title();
    }

    public static String randomAccessToken(){
        return faker.name().username();
    }

    public static LocalDateTime nowLastUpdatedDate(){
       return  LocalDateTime.now().withNano(0);
    }
}
