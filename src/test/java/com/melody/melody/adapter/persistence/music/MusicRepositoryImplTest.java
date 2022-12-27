package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.post.*;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
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

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        jpaRepository = Mockito.mock(MusicJpaRepository.class);
        mapper = Mockito.mock(MusicMapper.class);
        musicRepository = new MusicRepositoryImpl(jpaRepository, jpaQueryFactory, mapper);

        when(mapper.toModel(any(MusicData.class)))
                .thenCallRealMethod();

        when(mapper.toModel(any(MusicEntity.class)))
                .thenCallRealMethod();

        when(mapper.toEntity(any(Music.class)))
                .thenCallRealMethod();
    }

    @Test
    public void findById_ShouldReturnMusic_WhenExistMusic() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        MusicEntity musicEntity = TestMusicEntityGenerator.saveRandomMusicEntity(em, Music.Status.COMPLETION, userEntity);

        em.flush();
        em.clear();

        Identity musicId = Identity.from(musicEntity.getId());
        Optional<Music> actual = musicRepository.findById(musicId);
        assertTrue(actual.isPresent());
        assertEqualsEntityAndModel(musicEntity, actual.get());

        verify(mapper, times(1)).toModel(any(MusicData.class));
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExistMusic() {
        Identity musicId = TestMusicDomainGenerator.randomMusicId();

        Optional<Music> actual = musicRepository.findById(musicId);
        assertTrue(actual.isEmpty());

        verify(mapper, times(0)).toModel(any(MusicData.class));
    }

    @Test
    void findById_ShouldReturnEmpty_WhenDeletedMusic() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        MusicEntity musicEntity = TestMusicEntityGenerator.saveRandomMusicEntity(em, Music.Status.DELETED, userEntity);

        em.flush();
        em.clear();

        Identity musicId = Identity.from(musicEntity.getId());
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

        Identity user1Id = Identity.from(user1.getId());
        musicRepository.deleteByUserId(user1Id);

        assertDeleted(user1CompletionMusics, user1CompletionMusics.size());
        assertDeleted(user1ProgressMusics, user1ProgressMusics.size());
        assertDeleted(user1DeletedMusics, user1DeletedMusics.size());
        assertDeleted(user2Musics, 0);
    }

    @Test
    void findByUserId_ShuoldReturnList_WhenEverything() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> musicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, userEntity, Music.Status.COMPLETION, 11, 1);

        musicEntityMap.values()
                .stream()
                .limit(2)
                .forEach(m -> TestPostEntityGenerator.saveRandomPostEntity(em, true, true, LocalDateTime.now(), userEntity, m));

        musicEntityMap.values()
                .stream()
                .skip(2)
                .limit(3)
                .forEach(m -> TestPostEntityGenerator.saveRandomPostEntity(em, true, false, LocalDateTime.now(), userEntity, m));

        em.flush();
        em.clear();

        PagingResult<Music> actual = musicRepository.findByUserId(Identity.from(userEntity.getId()), MusicPublish.Everything, new PagingInfo<MusicSort>(0, 11, MusicSort.newest));
        assertEquals(11, actual.getCount());
        assertEquals(11, musicEntityMap.keySet().size());
        assertEquals(11, actual.getList().stream()
                .filter(m -> musicEntityMap.containsKey(m.getId().getValue()))
                .count());
    }

    @Test
    void findByUserId_ShuoldReturnList_WhenPublished() {
        Set<Long> publishedIdSet = new HashSet<>();
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> musicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, userEntity, Music.Status.COMPLETION, 11, 1);

        musicEntityMap.values()
                .stream()
                .limit(2)
                .forEach(m -> TestPostEntityGenerator.saveRandomPostEntity(em, true, true, LocalDateTime.now(), userEntity, m));

        musicEntityMap.values()
                .stream()
                .skip(2)
                .limit(3)
                .forEach(m -> {
                    TestPostEntityGenerator.saveRandomPostEntity(em, true, false, LocalDateTime.now(), userEntity, m);
                    publishedIdSet.add(m.getId());
                });

        em.flush();
        em.clear();

        PagingResult<Music> actual = musicRepository.findByUserId(Identity.from(userEntity.getId()), MusicPublish.Published, new PagingInfo<MusicSort>(0, 11, MusicSort.newest));
        assertEquals(3, actual.getCount());
        assertEquals(3, publishedIdSet.size());
        assertEquals(3, actual.getList().stream()
                .filter( m -> publishedIdSet.contains(m.getId().getValue()))
                .count());
    }


    @Test
    void findByUserId_ShuoldReturnList_WhenUnPublished() {
        Set<Long> unPublishedIdSet = new HashSet<>();
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> musicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, userEntity, Music.Status.COMPLETION, 11, 1);
        Collection<MusicEntity> musicEntities = musicEntityMap.values();

        musicEntities
                .stream()
                .limit(2)
                .forEach(m -> {
                    TestPostEntityGenerator.saveRandomPostEntity(em, true, true, LocalDateTime.now(), userEntity, m);
                    unPublishedIdSet.add(m.getId());
                });

        musicEntities
                .stream()
                .skip(2)
                .limit(3)
                .forEach(m -> TestPostEntityGenerator.saveRandomPostEntity(em, true, false, LocalDateTime.now(), userEntity, m));

        musicEntities
                .stream()
                .skip(5)
                .forEach(m -> unPublishedIdSet.add(m.getId()));

        em.flush();
        em.clear();

        PagingResult<Music> actual = musicRepository.findByUserId(Identity.from(userEntity.getId()), MusicPublish.Unpublished, new PagingInfo<MusicSort>(0, 11, MusicSort.newest));
        assertEquals(8, actual.getCount());
        assertEquals(8, unPublishedIdSet.size());
        assertEquals(8, actual.getList().stream()
                .filter( m -> unPublishedIdSet.contains(m.getId().getValue()))
                .count());

    }

    @Test
    void findByUserId_ShuoldReturnEmptyList_WhenNotExistUser() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        em.flush();
        em.clear();

        PagingResult<Music> actual = musicRepository.findByUserId(userId, MusicPublish.Everything, new PagingInfo<MusicSort>(0, 8, MusicSort.newest));
        assertEquals(0, actual.getCount());
    }

    @Test
    void findByUserId_ShuoldReturnEmptyList_WhenUserNotHaveMusic() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);

        UserEntity otherUserEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> otherUserMusicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, otherUserEntity, Music.Status.COMPLETION, 3, 5);

        em.flush();
        em.clear();

        PagingResult<Music> actual = musicRepository.findByUserId(Identity.from(userEntity.getId()), MusicPublish.Everything, new PagingInfo<MusicSort>(0, 8, MusicSort.newest));
        assertEquals(0, actual.getCount());
    }

    @Test
    void findByUserId_ShuoldReturnNewestSortedList_WhenNewestSort() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, MusicEntity> musicEntityMap = TestMusicEntityGenerator.saveRandomMusicEntitys(em, userEntity, Music.Status.COMPLETION, 20, 5);

        List<MusicEntity> sortedList = musicEntityMap.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? -1 : 1)
                .limit(8)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        PagingResult<Music> actual = musicRepository.findByUserId(Identity.from(userEntity.getId()), MusicPublish.Everything, new PagingInfo<MusicSort>(0, 8, MusicSort.newest));
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

        List<MusicEntity> sortedList = musicEntityMap.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? 1 : -1)
                .limit(8)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        PagingResult<Music> actual = musicRepository.findByUserId(Identity.from(userEntity.getId()), MusicPublish.Everything, new PagingInfo<MusicSort>(0, 8, MusicSort.oldest));
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

        PagingResult<Music> actual = musicRepository.findByUserId(Identity.from(userEntity.getId()), MusicPublish.Everything, new PagingInfo<MusicSort>(1, 8, MusicSort.newest));
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

        PagingResult<Music> actual = musicRepository.findByUserId(Identity.from(userEntity.getId()), MusicPublish.Everything, new PagingInfo<MusicSort>(2, 8, MusicSort.newest));
        assertEquals(4, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 4; i++){
            assertEqualsEntityAndModel(sortedList.get(i), actual.getList().get(i));
        }
    }

    private void assertEqualsEntityAndModel(MusicEntity entity, Music model){
        assertEquals(entity.getId(), model.getId().getValue());
        assertEquals(entity.getStatus(), model.getStatus());
        assertEquals(entity.getMusicUrl(), model.getMusicUrl().get().getValue());
        assertEquals(entity.getUserEntity().getId(), model.getUserId().getValue());
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