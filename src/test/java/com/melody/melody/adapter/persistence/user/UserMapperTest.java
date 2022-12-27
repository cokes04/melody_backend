package com.melody.melody.adapter.persistence.user;

import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class UserMapperTest {
    private static UserMapper mapper;

    @BeforeAll
    static void beforeAll() {
        mapper = new UserMapper();
    }

    @Test
    void toEntity_ShouldReturnEntity() {
        User user = TestUserDomainGenerator.randomUser();

        UserEntity actual = mapper.toEntity(user);
        assertEqualsModelAndEntity(user, actual);
    }

    @Test
    void toEntity_ShouldReturnNullIdEntity_WhenEmptyIdentity() {
        User user = TestUserDomainGenerator.randomUser(Identity.empty());

        UserEntity actual = mapper.toEntity(user);
        assertEqualsModelAndEntity(user, actual);
    }

    @Test
    void toModel_ShouldReturnModel() {
        UserEntity entity = TestUserEntityGenerator.randomUserEntity();

        User actual = mapper.toModel(entity);
        assertEqualsModelAndEntity(actual, entity);

    }

    @Test
    void toModel_entity_ShouldReturnEmptyIdentityModel_WhenNullId() {
        UserEntity entity = TestUserEntityGenerator.randomUserEntity();
        entity.setId(null);

        User actual = mapper.toModel(entity);
        assertEqualsModelAndEntity(actual, entity);
    }

    private void assertEqualsModelAndEntity(User user, UserEntity entity){
        if (user.getId().isEmpty())
            assertNull(entity.getId());
        else
            assertEquals(user.getId().getValue(), entity.getId());

        assertEquals(user.getNickName().getValue(), entity.getNickName());
        assertEquals(user.getEmail().getValue(), entity.getEmail());
        assertEquals(user.getPassword().getEncryptedString(), entity.getPassword());
        assertEquals(user.isWithdrawn(), entity.isWithdrawn());
        assertNotNull(entity.getCreatedDate());
        assertNotNull(entity.getLastActivityDate());
    }

}