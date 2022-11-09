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

    public static String randomLastName(){
        return faker.name().lastName();
    }

    public static String randomFirstName(){
        return faker.name().firstName();
    }

    public static String randomEmail(){
        return faker.internet().emailAddress();
    }

    public static Password randomPassword(){
        return new Password(
                faker.internet().password(8, 15, true, true, true)
        );
    }

    public static User randomUser(){
        return User.builder()
                .id(randomUserId())
                .lastName(randomLastName())
                .firstName(randomFirstName())
                .email(randomEmail())
                .password(randomPassword())
                .build();
    }

}
