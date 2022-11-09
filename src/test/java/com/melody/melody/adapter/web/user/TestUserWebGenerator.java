package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.user.request.CreateUserRequest;
import net.datafaker.Faker;

public class TestUserWebGenerator {
    private static final Faker faker = new Faker();

    public static CreateUserRequest randomCreateUserRequest(){
        return CreateUserRequest.builder()
                .lastName(randomLastName())
                .firstName(randomFirstName())
                .email(randomEmail())
                .password(randomPassword())
                .build();
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

    public static String randomPassword(){
        return faker.internet().password(8, 15, true, true, true);
    }
}
