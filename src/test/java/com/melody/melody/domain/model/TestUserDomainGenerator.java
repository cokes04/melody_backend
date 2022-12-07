package com.melody.melody.domain.model;

import net.datafaker.Faker;

import java.util.HashMap;

public class TestUserDomainGenerator {
    private static final Faker faker = new Faker();

    public static User.UserId randomUserId(){
        return new User.UserId(
                faker.number().numberBetween(33L, 33333L)
        );
    }

    public static User.NickName randomNickName(){
        return User.NickName.from(faker.name().firstName());
    }

    public static User.Email randomEmail(){
        return User.Email.from(faker.internet().emailAddress());
    }

    public static Password randomPassword(){
        return new Password(
                faker.internet().password(10, 20, true, true, true)
        );
    }

    public static User randomUser(){
        return randomUser(randomUserId());
    }

    public static User randomUser(User.UserId userId){
        return User.builder()
                .id(userId)
                .nickName(randomNickName())
                .email(randomEmail())
                .password(randomPassword())
                .withdrawn(false)
                .build();
    }
}
