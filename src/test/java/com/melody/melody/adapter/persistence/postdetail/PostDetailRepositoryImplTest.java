package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.post.TestPostEntityGenerator;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PersistenceTestConfig.class)
class PostDetailRepositoryImplTest {

    private PostDetailRepositoryImpl repository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        repository = new PostDetailRepositoryImpl(jpaQueryFactory);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById_ShouldReturnPostDetail() {
        PostEntity savedPostEntity = TestPostEntityGenerator.saveRandomPostEntity(em, true, false);
        Post.PostId postId = new Post.PostId(savedPostEntity.getId());

        Optional<PostDetail> optional = repository.findById(postId);
        assertTrue(optional.isPresent());

        PostDetail actual = optional.get();
        assertEqualsEntityAndDetail(savedPostEntity, actual);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUnSavedPost() {
        Post.PostId postId = TestPostDomainGenerator.randomPostId();
        assertNull(em.find(PostEntity.class, postId.getValue()));

        Optional<PostDetail> optional = repository.findById(postId);
        assertTrue(optional.isEmpty());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenDeletedPost() {
        PostEntity savedPostEntity = TestPostEntityGenerator.saveRandomPostEntity(em, true, false);
        Post.PostId postId = new Post.PostId(savedPostEntity.getId());

        savedPostEntity.setDeleted(true);
        em.persist(savedPostEntity);
        assertTrue(em.find(PostEntity.class, savedPostEntity.getId()).isDeleted());

        Optional<PostDetail> optional = repository.findById(postId);
        assertTrue(optional.isEmpty());
    }

    @Test
    void findByUserId_ShuoldReturnList_WhenEverythingOpen() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> openPostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 6);
        Map<Long, PostEntity> closePostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 3);

        PagingResult<PostDetail> actual = repository.findByUserId(new User.UserId(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(9, actual.getCount());
        actual.getList()
                .forEach( p -> {
                    if(p.isOpen())
                        assertEqualsEntityAndDetail(openPostMap.get(p.getId()), p);
                    else
                        assertEqualsEntityAndDetail(closePostMap.get(p.getId()), p);
                });
    }

    @Test
    void findByUserId_ShuoldReturnOpenList_WhenOnlyOpen() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> openPostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 6);
        Map<Long, PostEntity> closePostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 3);

        PagingResult<PostDetail> actual = repository.findByUserId(new User.UserId(userEntity.getId()), Open.OnlyOpen, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(6, actual.getCount());
        actual.getList().forEach( p -> assertEqualsEntityAndDetail(openPostMap.get(p.getId()), p));
        assertEquals(0, actual.getList().stream().filter( p -> closePostMap.containsKey(p.getId())).count());
    }

    @Test
    void findByUserId_ShuoldReturnCloseList_WhenOnlyClose() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> openPostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 6);
        Map<Long, PostEntity> closePostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 3);

        PagingResult<PostDetail> actual = repository.findByUserId(new User.UserId(userEntity.getId()), Open.OnlyClose, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(3, actual.getCount());
        actual.getList().forEach( p -> assertEqualsEntityAndDetail(closePostMap.get(p.getId()), p));
        assertEquals(0, actual.getList().stream().filter( p -> openPostMap.containsKey(p.getId())).count());
    }


    @Test
    void findByUserId_ShuoldReturnExcludeDeletedPost_WhenUserHaveDeletedPost() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> postMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 6);
        Map<Long, PostEntity> deletedPostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,true, 3);

        PagingResult<PostDetail> actual = repository.findByUserId(new User.UserId(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(6, actual.getCount());
        actual.getList().forEach( p -> assertEqualsEntityAndDetail(postMap.get(p.getId()), p));
        assertEquals(0, actual.getList().stream().filter( p -> deletedPostMap.containsKey(p.getId())).count());
    }

    @Test
    void findByUserId_ShuoldReturnEmptyList_WhenNotExistUser() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        PagingResult<PostDetail> actual = repository.findByUserId(userId, Open.Everything, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(0, actual.getCount());
    }

    @Test
    void findByUserId_ShuoldReturnEmptyList_WhenUserNotHavePost() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        User.UserId userId = new User.UserId(userEntity.getId());

        PagingResult<PostDetail> actual = repository.findByUserId(userId, Open.Everything, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(0, actual.getCount());
    }

    @Test
    void findByUserId_ShuoldReturnNewestSortedList_WhenNewestSort() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> map = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);

        List<PostEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? -1 : 1)
                .limit(8)
                .collect(Collectors.toList());

        PagingResult<PostDetail> actual = repository.findByUserId(new User.UserId(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(0, 8, PostSort.newest));
        assertEquals(8, actual.getCount());
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndDetail(sortedList.get(i), actual.getList().get(i));
        }
    }

    @Test
    void findByUserId_ShuoldReturnOldestSortedList_WhenOldestSort() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> map = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);

        List<PostEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? 1 : -1)
                .limit(8)
                .collect(Collectors.toList());

        PagingResult<PostDetail> actual = repository.findByUserId(new User.UserId(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(0, 8, PostSort.oldest));
        assertEquals(8, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndDetail(sortedList.get(i), actual.getList().get(i));
        }
    }

    @Test
    void findByUserId_ShuoldReturnSecondList_WhenSecondPage() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> map = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);

        List<PostEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? 1 : -1)
                .skip(8)
                .limit(8)
                .collect(Collectors.toList());

        PagingResult<PostDetail> actual = repository.findByUserId(new User.UserId(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(1, 8, PostSort.oldest));
        assertEquals(8, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndDetail(sortedList.get(i), actual.getList().get(i));
        }
    }

    @Test
    void findByUserId_ShuoldReturnLastList_WhenLastPage() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> map = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);

        List<PostEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? 1 : -1)
                .skip(16)
                .limit(8)
                .collect(Collectors.toList());

        PagingResult<PostDetail> actual = repository.findByUserId(new User.UserId(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(2, 8, PostSort.oldest));
        assertEquals(4, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 4; i++){
            assertEqualsEntityAndDetail(sortedList.get(i), actual.getList().get(i));
        }
    }

    void assertEqualsEntityAndDetail(PostEntity entity, PostDetail detail){
        assertEquals(entity.getId(), detail.getId());
        assertEquals(entity.getTitle(), detail.getTitle());
        assertEquals(entity.getContent(), detail.getContent());
        assertEquals(entity.getLikeCount(), detail.getLikeCount());
        assertEquals(entity.isOpen(), detail.isOpen());
        assertEquals(entity.isDeleted(), detail.isDeleted());
        assertTime(entity.getCreatedDate(), detail.getCreatedDate());

        assertEquals(entity.getMusicEntity().getEmotion(), detail.getEmotion());
        assertEquals(entity.getMusicEntity().getExplanation(), detail.getExplanation());
        assertEquals(entity.getMusicEntity().getImageUrl(), detail.getImageUrl());
        assertEquals(entity.getMusicEntity().getId(), detail.getMusicId());
        assertEquals(entity.getMusicEntity().getMusicUrl(), detail.getMusicUrl());
        assertEquals(entity.getMusicEntity().getStatus(), detail.getMusicStatus());

        assertEquals(entity.getUserEntity().getId(), detail.getUserId());
        assertEquals(entity.getUserEntity().getNickName(), detail.getNickname());
    }

    void assertTime(LocalDateTime expect, LocalDateTime actual){
        assertEquals(expect.getYear(), actual.getYear());
        assertEquals(expect.getMonth(), actual.getMonth());
        assertEquals(expect.getDayOfMonth(), actual.getDayOfMonth());
        assertEquals(expect.getHour(), actual.getHour());
        assertEquals(expect.getMinute(), actual.getMinute());
        assertEquals(expect.getSecond(), actual.getSecond());
    }
}