package com.melody.melody.adapter.persistence.user;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.post.TestPostEntityGenerator;
import com.melody.melody.application.service.user.TestSearchedUserServiceGenerator;
import com.melody.melody.domain.model.*;
import net.datafaker.Faker;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestUserEntityGenerator {
    private static final Faker faker = new Faker();

    public static Map<Long, UserEntity> saveRandomUserEntitys(TestEntityManager em, String keyword, String notIncludeKeyword, int count, int lastActivityDatePlusMinute){
        LocalDateTime now = LocalDateTime.now().withNano(0);
        return IntStream.range(0, count)
                .mapToObj(i -> saveRandomUserEntity(em, randomIncludedKeywordNickName(keyword, notIncludeKeyword), now.plusMinutes(lastActivityDatePlusMinute * i)))
                .collect(Collectors.toMap(
                        e -> e.getId(),
                        e -> e
                ));
    }
    public static UserEntity saveRandomUserEntity(TestEntityManager em, String nickName, LocalDateTime lastActivityDate){
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity(null, nickName, lastActivityDate);
        em.persist(userEntity);
        return userEntity;
    }


    public static UserEntity saveRandomUserEntity(TestEntityManager em){
        UserEntity userEntity = randomUserEntity(null, randomLastName(), LocalDateTime.now().withNano(0));
        em.persist(userEntity);
        return userEntity;
    }

    public static UserEntity randomUserEntity(){
        return randomUserEntity(randomId(), randomLastName(), LocalDateTime.now().withNano(0));
    }

    public static UserEntity randomUserEntity(Long userId, String nickName, LocalDateTime lastActivityDate){
        return UserEntity.builder()
                .id(userId)
                .nickName(nickName)
                .email(randomEmail())
                .password(randomPassword())
                .createdDate(LocalDateTime.now())
                .lastActivityDate(lastActivityDate)
                .withdrawn(false)
                .build();
    }

    public static String randomIncludedKeywordNickName(String keyword, String notIncludeKeyword){
        if (StringUtils.isBlank(notIncludeKeyword))
            return TestSearchedUserServiceGenerator.randomIncludedKeywordNickName(keyword);

        String nickName;
        while (true){
            nickName = TestSearchedUserServiceGenerator.randomIncludedKeywordNickName(keyword);
            if (!nickName.contains(notIncludeKeyword)) return nickName;
        }
    }
    public static Long randomId(){
        return TestUserDomainGenerator.randomUserId().getValue();
    }

    public static String randomLastName(){
        return TestUserDomainGenerator.randomNickName().getValue();
    }

    public static String randomEmail(){
        return TestUserDomainGenerator.randomEmail().getValue();
    }

    public static String randomPassword(){
        return TestUserDomainGenerator.randomPassword().getEncryptedString();
    }
}
