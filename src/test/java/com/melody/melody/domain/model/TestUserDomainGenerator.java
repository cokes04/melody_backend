package com.melody.melody.domain.model;

import net.datafaker.Faker;

public class TestUserDomainGenerator {
    private static final Faker faker = new Faker();

    public static Identity randomUserId(){
        return new Identity(
                faker.number().numberBetween(33L, 33333L)
        );
    }

    public static User.NickName randomNickName(){
        return User.NickName.from(faker.name().firstName());
    }

    public static User.Email randomEmail(){
        return User.Email.from(faker.internet().emailAddress());
    }

    public static User.Password randomPassword(){
        return new User.Password(
                faker.internet().password(10, 20, true, true, true)
        );
    }

    public static User randomUser(){
        return randomUser(randomUserId());
    }

    public static User randomUser(Identity userId){
        return User.builder()
                .id(userId)
                .nickName(randomNickName())
                .email(randomEmail())
                .password(randomPassword())
                .withdrawn(false)
                .build();
    }
}
