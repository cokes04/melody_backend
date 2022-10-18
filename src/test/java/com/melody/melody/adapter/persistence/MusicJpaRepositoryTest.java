package com.melody.melody.adapter.persistence;

import com.melody.melody.adapter.persistence.entity.MusicEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.HashSet;
import java.util.Optional;

import static java.util.Collections.singletonList;
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
    @EntityScan("com.melody.melody.adapter.persistence.entity")
    static class Config {
    }

    @Test
    void save() {
        MusicEntity musicEntity = TestEntityGenerator.randomMusicEntity();
        musicEntity.setId(null);

        MusicEntity actual = repository.save(musicEntity);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void findByIdReturnMusic() {
        MusicEntity expected = TestEntityGenerator.randomMusicEntity();
        expected.setId(null);
        expected = entityManager.persistAndFlush(expected);

        Optional<MusicEntity> actual = repository.findById(expected.getId());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findByIdReturnEmpty() {
        Optional<MusicEntity> actual = repository.findById(0L);

        assertTrue(actual.isEmpty());
    }
}