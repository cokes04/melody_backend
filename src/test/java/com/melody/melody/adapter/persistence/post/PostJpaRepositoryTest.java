package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.adapter.persistence.user.UserJpaRepository;
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
class PostJpaRepositoryTest {
    @Autowired
    private PostJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;
    
    @Configuration
    @AutoConfigurationPackage
    @EntityScan("com.melody.melody.adapter.persistence")
    static class Config{
        
    }

    @Test
    void save_ShouldReturnPostEntityWithId() {
        PostEntity postEntity = TestPostEntityGenerator.randomPostEntity();

        postEntity.getUserEntity().setId(null);
        postEntity.setUserEntity(entityManager.persistAndFlush(postEntity.getUserEntity()));

        postEntity.getMusicEntity().setId(null);
        postEntity.setMusicEntity(entityManager.persistAndFlush(postEntity.getMusicEntity()));

        postEntity.setId(null);
        assertNull(postEntity.getId());

        PostEntity actual = repository.save(postEntity);

        assertNotNull(actual.getId());
        assertTrue(actual.getId() > 0);

        postEntity.setId(actual.getId());
        assertEquals(postEntity, actual);
    }

    @Test
    void findById_ShouldReturnPostEntity_WhenExistPostEntity() {
        PostEntity expect = TestPostEntityGenerator.randomPostEntity();

        expect.getUserEntity().setId(null);
        expect.setUserEntity(entityManager.persistAndFlush(expect.getUserEntity()));

        expect.getMusicEntity().setId(null);
        expect.setMusicEntity(entityManager.persistAndFlush(expect.getMusicEntity()));

        expect.setId(null);
        expect = entityManager.persistAndFlush(expect);

        Optional<PostEntity> actual = repository.findById(expect.getId());

        assertTrue(actual.isPresent());
        assertEquals(expect, actual.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExistPostEntity() {
        long postId = TestPostEntityGenerator.randomId();
        Optional<PostEntity> actual = repository.findById(postId);
        assertTrue(actual.isEmpty());
    }
}