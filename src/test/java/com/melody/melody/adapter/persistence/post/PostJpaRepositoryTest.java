package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.music.TestMusicEntityGenerator;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.adapter.persistence.user.UserJpaRepository;
import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PersistenceTestConfig.class)
class PostJpaRepositoryTest {
    @Autowired
    private PostJpaRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void save_ShouldReturnPostEntityWithId() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        MusicEntity musicEntity = TestMusicEntityGenerator.saveRandomMusicEntity(em, Music.Status.COMPLETION, userEntity);
        em.flush();
        em.clear();

        PostEntity postEntity = TestPostEntityGenerator.randomPostEntity(null, true, false, LocalDateTime.now(), userEntity, musicEntity);

        PostEntity actual = repository.save(postEntity);

        assertNotNull(actual.getId());
        assertTrue(actual.getId() > 0);

        postEntity.setId(actual.getId());
        assertEquals(postEntity, actual);
    }

    @Test
    void findById_ShouldReturnPostEntity_WhenExistPostEntity() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        MusicEntity musicEntity = TestMusicEntityGenerator.saveRandomMusicEntity(em, Music.Status.COMPLETION, userEntity);
        PostEntity postEntity = TestPostEntityGenerator.saveRandomPostEntity(em, true, false, LocalDateTime.now(), userEntity, musicEntity);
        em.flush();
        em.clear();

        Optional<PostEntity> actual = repository.findById(postEntity.getId());
        assertTrue(actual.isPresent());
        assertEquals(postEntity, actual.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExistPostEntity() {
        long postId = TestPostEntityGenerator.randomId();
        Optional<PostEntity> actual = repository.findById(postId);
        assertTrue(actual.isEmpty());
    }
}