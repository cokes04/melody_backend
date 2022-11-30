package com.melody.melody.adapter.persistence.user;

import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.melody.melody.domain.model.TestUserDomainGenerator.randomUser;
import static org.junit.jupiter.api.Assertions.*;


class UserMapperTest {
    private static UserMapper mapper;

    @BeforeAll
    static void beforeAll() {
        mapper = new UserMapper();
    }

    @Test
    void toEntity_ShouldReturnEntity() {
        User user = randomUser();

        UserEntity actual = mapper.toEntity(user);

        assertTrue(user.getId().isPresent());
        assertEquals(user.getId().get().getValue(), actual.getId());
        assertEquals(user.getNickName(), actual.getNickName());
        assertEquals(user.getEmail(), actual.getEmail());
        assertEquals(user.getPassword().getEncryptedString(), actual.getPassword());
        assertEquals(user.isWithdrawn(), actual.isWithdrawn());
    }

    @Test
    void toModel_ShouldReturnModel() {
        UserEntity entity = TestUserEntityGenerator.randomUserEntity();

        User actual = mapper.toModel(entity);

        assertTrue(actual.getId().isPresent());
        assertEquals(entity.getId(), actual.getId().get().getValue());
        assertEquals(entity.getNickName(), actual.getNickName());
        assertEquals(entity.getEmail(), actual.getEmail());
        assertEquals(entity.getPassword(), actual.getPassword().getEncryptedString());
        assertEquals(entity.isWithdrawn(), actual.isWithdrawn());

    }
}