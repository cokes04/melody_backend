package com.melody.melody.application.service.user;

import com.melody.melody.domain.model.TestUserDomainGenerator;
import net.datafaker.Faker;

public class TestUserServiceGenerator {
    private static final Faker faker = new Faker();

    public static CreateUserService.Command randomCreateUserCommand() {
        return new CreateUserService.Command(
                randomNickName(),
                randomEmail(),
                randomPassword()
        );
    }

    public static CreateUserService.Result randomCreateUserResult() {
        return new CreateUserService.Result(
                TestUserDomainGenerator.randomUser()
        );
    }

    public static String randomNickName(){
        return faker.name().lastName();
    }

    public static String randomEmail(){
        return faker.internet().emailAddress();
    }

    public static String randomPassword(){
        return  faker.internet().password(8, 15, true, true, true);

    }
}
