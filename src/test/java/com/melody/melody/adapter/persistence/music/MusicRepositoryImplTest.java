package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.*;
import java.util.stream.Collectors;

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
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        MusicEntity musicEntity = TestMusicEntityGenerator.saveRandomMusicEntity(em, Music.Status.COMPLETION, userEntity);

        Music.MusicId musicId = new Music.MusicId(musicEntity.getId());

        when(mapper.toModel(any(MusicData.class)))
                .thenCallRealMethod();

        Optional<Music> actual = musicRepository.findById(musicId);
        assertTrue(actual.isPresent());
        assertEqualsEntityAndModel(musicEntity, actual.get());

        verify(mapper, times(1)).toModel(any(MusicData.class));
    }

    @Test
    void getById_ShouldReturnEmpty_WhenNotExistMusic() {
        Music.MusicId musicId = TestMusicDomainGenerator.randomMusicId();


        Optional<Music> actual = musicRepository.findById(musicId);
        assertTrue(actual.isEmpty());

        verify(mapper, times(0)).toModel(any(MusicData.class));
    }
    @Test
    void getById_ShouldReturnEmpty_WhenDeletedMusic() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        MusicEntity musicEntity = TestMusicEntityGenerator.saveRandomMusicEntity(em, Music.Status.DELETED, userEntity);

        Music.MusicId musicId = new Music.MusicId(musicEntity.getId());

        Optional<Music> actual = musicRepository.findById(musicId);
        assertTrue(actual.isEmpty());

        verify(mapper, times(0)).toModel(any(MusicData.class));
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

        String user1Id = String.valueOf(user1.getId());
        musicRepository.deleteByUserId(user1Id);

        assertDeleted(user1CompletionMusics, user1CompletionMusics.size());
        assertDeleted(user1ProgressMusics, user1ProgressMusics.size());
        assertDeleted(user1DeletedMusics, user1DeletedMusics.size());
        assertDeleted(user2Musics, 0);
    }

    @Test
    void findByUserId_ShuoldReturnEmptyList_WhenNotExistUser() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();
        String userIdString = String.valueOf(userId);

        em.flush();
        em.clear();

        PagingResult<Music> actual = musicRepository.findByUserId(userIdString, MusicPublish.Everything, new PagingInfo<MusicSort>(0, 8, MusicSort.newest));
        assertEquals(0, actual.getCount());
    }

    @Test
    void findByUserId_ShuoldReturnEmptyList_WhenUserNotHaveMusic() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        String userIdString = String.valueOf(userEntity.getId());

        UserEntity otherUserEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> otherUserMusicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, otherUserEntity, Music.Status.COMPLETION, 3, 5);

        em.flush();
        em.clear();

        PagingResult<Music> actual = musicRepository.findByUserId(userIdString, MusicPublish.Everything, new PagingInfo<MusicSort>(0, 8, MusicSort.newest));
        assertEquals(0, actual.getCount());
    }

    @Test
    void findByUserId_ShuoldReturnNewestSortedList_WhenNewestSort() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> musicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, userEntity, Music.Status.COMPLETION, 20, 5);
        String userIdString = String.valueOf(userEntity.getId());

        List<MusicEntity> sortedList = musicEntityMap.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? -1 : 1)
                .limit(8)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        when(mapper.toModel(any(MusicData.class)))
                .thenCallRealMethod();

        PagingResult<Music> actual = musicRepository.findByUserId(userIdString, MusicPublish.Everything, new PagingInfo<MusicSort>(0, 8, MusicSort.newest));
        assertEquals(8, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndModel(sortedList.get(i), actual.getList().get(i));
        }
    }

    @Test
    void findByUserId_ShuoldReturnOldestSortedList_WhenOldestSort() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> musicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, userEntity, Music.Status.COMPLETION, 20, 5);
        String userIdString = String.valueOf(userEntity.getId());

        List<MusicEntity> sortedList = musicEntityMap.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? 1 : -1)
                .limit(8)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        when(mapper.toModel(any(MusicData.class)))
                .thenCallRealMethod();

        PagingResult<Music> actual = musicRepository.findByUserId(userIdString, MusicPublish.Everything, new PagingInfo<MusicSort>(0, 8, MusicSort.oldest));
        assertEquals(8, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndModel(sortedList.get(i), actual.getList().get(i));
        }
    }

    @Test
    void findByUserId_ShuoldReturnSecondList_WhenSecondPage() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> musicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, userEntity, Music.Status.COMPLETION, 20, 5);

        List<MusicEntity> sortedList = musicEntityMap.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? -1 : 1)
                .skip(8)
                .limit(8)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        when(mapper.toModel(any(MusicData.class)))
                .thenCallRealMethod();

        PagingResult<Music> actual = musicRepository.findByUserId(String.valueOf(userEntity.getId()), MusicPublish.Everything, new PagingInfo<MusicSort>(1, 8, MusicSort.newest));
        assertEquals(8, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndModel(sortedList.get(i), actual.getList().get(i));
        }
    }

    @Test
    void findByUserId_ShuoldReturnLastList_WhenLastPage() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> musicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, userEntity, Music.Status.COMPLETION, 20, 5);

        List<MusicEntity> sortedList = musicEntityMap.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? -1 : 1)
                .skip(16)
                .limit(8)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        when(mapper.toModel(any(MusicData.class)))
                .thenCallRealMethod();

        PagingResult<Music> actual = musicRepository.findByUserId(String.valueOf(userEntity.getId()), MusicPublish.Everything, new PagingInfo<MusicSort>(2, 8, MusicSort.newest));
        assertEquals(4, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 4; i++){
            assertEqualsEntityAndModel(sortedList.get(i), actual.getList().get(i));
        }
    }

    private void assertEqualsEntityAndModel(MusicEntity entity, Music model){
        assertEquals(entity.getId(), model.getId().get().getValue());
        assertEquals(entity.getStatus(), model.getStatus());
        assertEquals(entity.getMusicUrl(), model.getMusicUrl().get().getValue());
        assertEquals(entity.getUserId(), model.getUserId());
        assertEquals(entity.getImageUrl(), model.getImageUrl().getValue());
        assertEquals(entity.getExplanation(), model.getExplanation().getValue());
        assertEquals(entity.getEmotion(), model.getEmotion());

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