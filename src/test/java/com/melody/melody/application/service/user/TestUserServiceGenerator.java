package com.melody.melody.application.service.user;

import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.Password;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import net.datafaker.Faker;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

import static com.melody.melody.domain.model.TestMusicDomainGenerator.randomMusic;

public class TestUserServiceGenerator {
    private static final Faker faker = new Faker();

    public static CreateUserService.Command randomCreateUserCommand() {
        return new CreateUserService.Command(
                randomLastName(),
                randomFirstName(),
                randomEmail(),
                randomPassword()
        );
    }

    public static CreateUserService.Result randomCreateUserResult() {
        return new CreateUserService.Result(
                TestUserDomainGenerator.randomUser()
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

    public static String randomPassword(){
        return  faker.internet().password(8, 15, true, true, true);

    }
}
