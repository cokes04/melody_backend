package com.melody.melody.adapter.persistence.music;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class MusicJpaRepositoryTest {
    @Autowired
    private MusicJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Configuration
    @AutoConfigurationPackage
    @EntityScan("com.melody.melody.adapter.persistence.music")
    static class Config {
    }

    @Test
    void save_ShouldReturnEntityWithId() {
        MusicEntity musicEntity = TestMusicEntityGenerator.randomMusicEntity();
        musicEntity.setId(null);

        MusicEntity actual = repository.save(musicEntity);

        assertNotNull(actual.getId());
        assertTrue(actual.getId() > 0);
    }

    @Test
    void findById_ShouldReturnMusic() {
        MusicEntity expected = TestMusicEntityGenerator.randomMusicEntity();
        expected.setId(null);
        expected = entityManager.persistAndFlush(expected);

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