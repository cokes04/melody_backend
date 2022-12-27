package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.post.TestPostEntityGenerator;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PersistenceTestConfig.class)
class PostDetailDaoTest {
    private PostDetailDao dao;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        dao = new PostDetailDao(jpaQueryFactory);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById_ShouldReturnPostDetail() {
        PostEntity savedPostEntity = TestPostEntityGenerator.saveRandomPostEntity(em, true, false);
        Identity postId = Identity.from(savedPostEntity.getId());

        em.flush();
        em.clear();

        Optional<PostDetail> optional = dao.findById(postId);
        assertTrue(optional.isPresent());

        PostDetail actual = optional.get();
        assertEqualsEntityAndDetail(savedPostEntity, actual);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUnSavedPost() {
        Identity postId = TestPostDomainGenerator.randomPostId();
        assertNull(em.find(PostEntity.class, postId.getValue()));

        em.flush();
        em.clear();

        Optional<PostDetail> optional = dao.findById(postId);
        assertTrue(optional.isEmpty());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenDeletedPost() {
        PostEntity savedPostEntity = TestPostEntityGenerator.saveRandomPostEntity(em, true, false);
        savedPostEntity.setDeleted(true);

        Identity postId = Identity.from(savedPostEntity.getId());

        em.persist(savedPostEntity);

        em.flush();
        em.clear();

        Optional<PostDetail> optional = dao.findById(postId);
        assertTrue(optional.isEmpty());
    }

    @Test
    void findByUserId_ShouldReturnList_WhenEverything() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> openPostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 6);
        Map<Long, PostEntity> closePostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 3);

        em.flush();
        em.clear();

        List<PostDetail> actual = dao.findByUserId(Identity.from(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(9, actual.size());
        actual.forEach( p -> {
            if(p.isOpen())
                assertEqualsEntityAndDetail(openPostMap.get(p.getId()), p);
            else
                assertEqualsEntityAndDetail(closePostMap.get(p.getId()), p);
        });
    }

    @Test
    void findByUserId_ShouldReturnOpenList_WhenOnlyOpen() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> openPostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 6);
        Map<Long, PostEntity> closePostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 3);

        em.flush();
        em.clear();

        List<PostDetail> actual = dao.findByUserId(Identity.from(userEntity.getId()), Open.OnlyOpen, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(6, actual.size());
        actual.forEach( p -> assertEqualsEntityAndDetail(openPostMap.get(p.getId()), p));
        assertEquals(0, actual.stream().filter( p -> closePostMap.containsKey(p.getId())).count());
    }

    @Test
    void findByUserId_ShouldReturnCloseList_WhenOnlyClose() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> openPostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 6);
        Map<Long, PostEntity> closePostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 3);

        em.flush();
        em.clear();

        List<PostDetail> actual = dao.findByUserId(Identity.from(userEntity.getId()), Open.OnlyClose, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(3, actual.size());
        actual.forEach( p -> assertEqualsEntityAndDetail(closePostMap.get(p.getId()), p));
        assertEquals(0, actual.stream().filter( p -> openPostMap.containsKey(p.getId())).count());
    }


    @Test
    void findByUserId_ShouldReturnExcludeDeletedPost_WhenUserHaveDeletedPost() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> postMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 6);
        Map<Long, PostEntity> deletedPostMap = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,true, 3);

        em.flush();
        em.clear();

        List<PostDetail> actual = dao.findByUserId(Identity.from(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(6, actual.size());
        actual.forEach( p -> assertEqualsEntityAndDetail(postMap.get(p.getId()), p));
        assertEquals(0, actual.stream().filter( p -> deletedPostMap.containsKey(p.getId())).count());
    }

    @Test
    void findByUserId_ShouldReturnEmptyList_WhenNotExistUser() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        List<PostDetail> actual = dao.findByUserId(userId, Open.Everything, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(0, actual.size());
    }

    @Test
    void findByUserId_ShouldReturnEmptyList_WhenUserNotHavePost() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Identity userId = Identity.from(userEntity.getId());

        em.flush();
        em.clear();

        List<PostDetail> actual = dao.findByUserId(userId, Open.Everything, new PagingInfo<PostSort>(0, 9, PostSort.newest));
        assertEquals(0, actual.size());
    }

    @Test
    void findByUserId_ShouldReturnNewestSortedList_WhenNewestSort() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> map = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);

        em.flush();
        em.clear();

        List<PostEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? -1 : 1)
                .limit(8)
                .collect(Collectors.toList());

        List<PostDetail> actual = dao.findByUserId(Identity.from(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(0, 8, PostSort.newest));
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndDetail(sortedList.get(i), actual.get(i));
        }
    }

    @Test
    void findByUserId_ShouldReturnOldestSortedList_WhenOldestSort() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> map = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);

        em.flush();
        em.clear();

        List<PostEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? 1 : -1)
                .limit(8)
                .collect(Collectors.toList());

        List<PostDetail> actual = dao.findByUserId(Identity.from(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(0, 8, PostSort.oldest));
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndDetail(sortedList.get(i), actual.get(i));
        }
    }

    @Test
    void findByUserId_ShouldReturnSecondList_WhenSecondPage() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> map = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);

        em.flush();
        em.clear();

        List<PostEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? 1 : -1)
                .skip(8)
                .limit(8)
                .collect(Collectors.toList());

        List<PostDetail> actual = dao.findByUserId(Identity.from(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(1, 8, PostSort.oldest));
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndDetail(sortedList.get(i), actual.get(i));
        }
    }

    @Test
    void findByUserId_ShouldReturnLastList_WhenLastPage() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> map = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);

        em.flush();
        em.clear();

        List<PostEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getCreatedDate().isBefore(b.getCreatedDate()) ? 1 : -1)
                .skip(16)
                .limit(8)
                .collect(Collectors.toList());

        List<PostDetail> actual = dao.findByUserId(Identity.from(userEntity.getId()), Open.Everything, new PagingInfo<PostSort>(2, 8, PostSort.oldest));

        for (int i = 0; i < 4; i++){
            assertEqualsEntityAndDetail(sortedList.get(i), actual.get(i));
        }
    }

    @Test
    void findTotalSizeByUserId_ShouldReturnEverythingSiseExcludeDeleted_WhenHaveDeletedPost() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 10, 10);

        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,true, 5, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,true, 4, 10);

        em.flush();
        em.clear();

        long actual = dao.findTotalSizeByUserId(Identity.from(userEntity.getId()), Open.Everything);
        assertEquals(30, actual);
    }

    @Test
    void findTotalSizeByUserId_ShouldReturnEverythingSise_WhenEverything() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 10, 10);

        em.flush();
        em.clear();

        long actual = dao.findTotalSizeByUserId(Identity.from(userEntity.getId()), Open.Everything);
        assertEquals(30, actual);
    }

    @Test
    void findTotalSizeByUserId_ShouldReturnOpenSise_WhenOnlyOpen() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 10, 10);

        em.flush();
        em.clear();

        long actual = dao.findTotalSizeByUserId(Identity.from(userEntity.getId()), Open.OnlyOpen);
        assertEquals(20, actual);
    }

    @Test
    void findTotalSizeByUserId_ShouldReturnCloseSise_WhenClose() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 10, 10);

        em.flush();
        em.clear();

        long actual = dao.findTotalSizeByUserId(Identity.from(userEntity.getId()), Open.OnlyClose);
        assertEquals(10, actual);
    }

    @Test
    void findTotalSizeByUserId_ShouldReturnZero_WhenNotExistUser() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        long actual = dao.findTotalSizeByUserId(userId, Open.OnlyClose);
        assertEquals(0, actual);
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