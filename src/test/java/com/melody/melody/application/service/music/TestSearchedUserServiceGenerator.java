package com.melody.melody.application.service.music;

import com.melody.melody.application.dto.SearchedUser;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import net.datafaker.Faker;

public class TestSearchedUserServiceGenerator {
    private final static Faker faker = new Faker();

    public static SearchedUser randomSearchedUser(String keyword){
        String name = randomIncludedKeywordNickName(keyword);

        return SearchedUser.builder()
                .userId(TestUserDomainGenerator.randomUserId().getValue())
                .nickName(name)
                .build();
    }

    public static SearchedUser randomSearchedUser(){
        return SearchedUser.builder()
                .userId(TestUserDomainGenerator.randomUserId().getValue())
                .nickName(TestUserDomainGenerator.randomNickName().getValue())
                .build();
    }

    public static String randomIncludedKeywordNickName(String keyword){
        int randomIndex = faker.number().numberBetween(0, 8);
        switch (randomIndex){
            case 0:
                return faker.name().firstName() + keyword;
            case 1:
                return faker.name().lastName() + keyword;
            case 2:
                return keyword + faker.name().firstName();
            case 3:
                return keyword + faker.name().firstName();
            case 4:
                return faker.name().firstName() + keyword + faker.name().lastName();
            case 5:
                return keyword + faker.name().name();
            case 6:
                return keyword + faker.name().name() + faker.name().name();
            default:
                return faker.name().firstName() + keyword + faker.name().username();
        }
    }
}
