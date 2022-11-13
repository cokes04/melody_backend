package com.melody.melody.adapter.persistence.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserJpaRepositoryTest {
    @Autowired
    private UserJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Configuration
    @AutoConfigurationPackage
    @EntityScan("com.melody.melody.adapter.persistence.user")
    static class Config {
    }

    @Test
    void save_ShouldReturnEntityWithId() {
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity();
        userEntity.setId(null);

        UserEntity actual = repository.save(userEntity);

        assertNotNull(actual.getId());
        assertTrue(actual.getId() > 0);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenUnsavedEmail() {
        String email = TestUserEntityGenerator.randomEmail();

        boolean actual = repository.existsByEmail(email);
        assertFalse(actual);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenSavedEmail() {
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity();
        userEntity.setId(null);
        userEntity = entityManager.persistAndFlush(userEntity);

        boolean actual = repository.existsByEmail(userEntity.getEmail());
        assertTrue(actual);
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenUnsavedUserEmail() {
        String email = TestUserEntityGenerator.randomEmail();

        Optional<UserEntity> actual = repository.findByEmail(email);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findByEmail_ShouldReturnEntity_WhenSavedUserEmail() {
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity();
        userEntity = entityManager.merge(userEntity);
        String email = userEntity.getEmail();

        Optional<UserEntity> actual = repository.findByEmail(email);

        assertTrue(actual.isPresent());
        assertEquals(userEntity, actual.get());
    }
}