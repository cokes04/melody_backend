package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.post.*;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import com.melody.melody.domain.model.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(PersistenceTestConfig.class)
class MusicRepositoryImplTest {
    private MusicRepositoryImpl musicRepository;
    private MusicJpaRepository jpaRepository;
    private MusicMapper mapper;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        jpaRepository = Mockito.mock(MusicJpaRepository.class);
        mapper = Mockito.mock(MusicMapper.class);
        musicRepository = new MusicRepositoryImpl(jpaRepository, jpaQueryFactory, mapper);
    }

    @Test
    public void getById_ShouldReturnMusic_WhenExistMusic() {
        Music expected = TestMusicDomainGenerator.randomMusic();
        MusicEntity entity = TestMusicEntityGenerator.randomMusicEntity();
        Music.MusicId musicId = expected.getId().get();
        entity.setStatus(Music.Status.COMPLETION);

        when(jpaRepository.findById(eq(musicId.getValue())))
                .thenReturn(Optional.of(entity));

        when(mapper.toModel(eq(entity)))
                .thenReturn(expected);

        Optional<Music> actual = musicRepository.getById(musicId);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());

        verify(jpaRepository, times(1)).findById(eq(musicId.getValue()));
        verify(mapper, times(1)).toModel(eq(entity));
    }

    @Test
    void getById_ShouldReturnEmpty_WhenNotExistMusic() {
        Music.MusicId musicId = TestMusicDomainGenerator.randomMusicId();

        when(jpaRepository.findById(eq(musicId.getValue())))
                .thenReturn(Optional.empty());

        Optional<Music> actual = musicRepository.getById(musicId);

        assertTrue(actual.isEmpty());

        verify(jpaRepository, times(1)).findById(eq(musicId.getValue()));
        verify(mapper, times(0)).toModel(any(MusicEntity.class));
    }
    @Test
    void getById_ShouldReturnEmpty_WhenDeletedMusic() {
        MusicEntity entity = TestMusicEntityGenerator.randomMusicEntity();
        Music.MusicId musicId = new Music.MusicId(entity.getId());
        entity.setStatus(Music.Status.DELETED);

        when(jpaRepository.findById(eq(musicId.getValue())))
                .thenReturn(Optional.of(entity));

        Optional<Music> actual = musicRepository.getById(musicId);

        assertTrue(actual.isEmpty());

        verify(jpaRepository, times(1)).findById(eq(musicId.getValue()));
        verify(mapper, times(0)).toModel(any(MusicEntity.class));
    }

    @Test
    void save_ShouldReturnMusic() {
        Music expected = TestMusicDomainGenerator.randomMusic();
        MusicEntity entity = TestMusicEntityGenerator.randomMusicEntity();

        when(mapper.toEntity(eq(expected)))
                .thenReturn(entity);

        when(jpaRepository.save(eq(entity)))
                .thenReturn(entity);

        when(mapper.toModel(eq(entity)))
                .thenReturn(expected);

        Music actual = musicRepository.save(expected);

        assertEquals(expected, actual);

        verify(mapper, times(1)).toEntity(eq(expected));
        verify(jpaRepository, times(1)).save(eq(entity));
        verify(mapper, times(1)).toModel(eq(entity));
    }


    @Test
    void deleteByUserId_ShouldDeletedUsersMusic() {
        UserEntity user1 = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> user1CompletionMusics = TestMusicEntityGenerator.saveRandomMusicEntitys(em, user1, Music.Status.COMPLETION, 3);
        Map<Long, MusicEntity> user1ProgressMusics = TestMusicEntityGenerator.saveRandomMusicEntitys(em, user1, Music.Status.PROGRESS, 2);
        Map<Long, MusicEntity> user1DeletedMusics = TestMusicEntityGenerator.saveRandomMusicEntitys(em, user1,Music.Status.DELETED, 1);

        UserEntity user2 = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> user2Musics = TestMusicEntityGenerator.saveRandomMusicEntitys(em, user2,Music.Status.COMPLETION, 4);

        em.flush();
        em.clear();

        User.UserId user1Id = new User.UserId(user1.getId());
        musicRepository.deleteByUserId(user1Id);

        assertDeleted(user1CompletionMusics, user1CompletionMusics.size());
        assertDeleted(user1ProgressMusics, user1ProgressMusics.size());
        assertDeleted(user1DeletedMusics, user1DeletedMusics.size());
        assertDeleted(user2Musics, 0);
    }

    private void assertDeleted(Map<Long, MusicEntity> musics, int deletedCount){
        assertEquals(
                deletedCount,
                musics.keySet().stream()
                        .map(id -> em.find(MusicEntity.class, id))
                        .filter(m -> Music.Status.DELETED.equals(m.getStatus()))
                        .count()
        );
    }
}