package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PersistenceTestConfig.class)
@ActiveProfiles("dbtest")
class MusicJpaRepositoryTest {
    @Autowired
    private MusicJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Configuration
    @AutoConfigurationPackage
    @EntityScan("com.melody.melody.adapter.persistence")
    static class Config {
    }

    @Test
    void save_ShouldReturnEntityWithId() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(entityManager);
        MusicEntity musicEntity = TestMusicEntityGenerator.randomMusicEntity();
        musicEntity.setId(null);
        musicEntity.setUserEntity(UserEntity.builder().id(userEntity.getId()).build());

        MusicEntity actual = repository.save(musicEntity);

        assertNotNull(actual.getId());
        assertTrue(actual.getId() > 0);
        assertEquals(userEntity, entityManager.find(UserEntity.class, userEntity.getId()));
    }

    @Test
    void save_ShouldException_WhenUnSavedUserEntity() {
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity();

        MusicEntity musicEntity = TestMusicEntityGenerator.randomMusicEntity();
        musicEntity.setId(null);
        musicEntity.setUserEntity(userEntity);

        entityManager.flush();
        entityManager.clear();

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.save(musicEntity)
        );
    }


    @Test
    void save_ShouldException_WhenNullUserEntity() {
        MusicEntity musicEntity = TestMusicEntityGenerator.randomMusicEntity();
        musicEntity.setId(null);
        musicEntity.setUserEntity(null);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.save(musicEntity)
        );
    }

    @Test
    void findById_ShouldReturnMusic() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(entityManager);
        MusicEntity expected = TestMusicEntityGenerator.saveRandomMusicEntity(entityManager, Music.Status.COMPLETION, userEntity);

        entityManager.flush();
        entityManager.clear();

        Optional<MusicEntity> actual = repository.findById(expected.getId());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findById_ShouldReturnEmpty() {
        Optional<MusicEntity> actual = repository.findById(0L);

        assertTrue(actual.isEmpty());
    }
}