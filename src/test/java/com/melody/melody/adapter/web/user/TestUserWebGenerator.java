package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.user.request.CreateUserRequest;
import com.melody.melody.adapter.web.user.request.UpdateUserRequest;
import net.datafaker.Faker;

public class TestUserWebGenerator {
    private static final Faker faker = new Faker();

    public static UpdateUserRequest randomUpdateUserRequest(){
        return UpdateUserRequest.builder()
                .nickName(randomNickName())
                .build();
    }

    public static CreateUserRequest randomCreateUserRequest(){
        return CreateUserRequest.builder()
                .nickName(randomNickName())
                .email(randomEmail())
                .password(randomPassword())
                .build();
    }

    public static String randomNickName(){
        return faker.name().lastName();
    }

    public static String randomEmail(){
        return faker.internet().emailAddress();
    }

    public static String randomPassword(){
        return faker.internet().password(8, 15, true, true, true);
    }
}
