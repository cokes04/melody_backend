package com.melody.melody.adapter.persistence.post.pagination;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.post.size.SizeInfo;
import com.melody.melody.adapter.persistence.post.TestPostEntityGenerator;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.querydsl.jpa.impl.JPAQueryFactory;
import groovyjarjarantlr4.v4.runtime.misc.IntegerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(PersistenceTestConfig.class)
@ActiveProfiles("dbtest")
class PostPaginationDaoTest {

    private PostPaginationDao dao;
    private UserPostPaginationCache cache;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        cache = Mockito.mock(UserPostPaginationCache.class);
        dao = new PostPaginationDao(jpaQueryFactory, cache);

    }

    @Test
    void find_ShouldReturnOffset0_WhenFirstPage() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 20, PostSort.newest);


        when(cache.get(userId, SizeInfo.Open, true, 0))
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPagination(
                                        PostPagination.builder()
                                                .startPostId(0L)
                                                .offset(20)
                                                .startInclude(true)
                                                .build()
                                )
                                .neededIndexs(0)
                                .countPerIndex(20)
                                .build()
                );

        PostPagination postPagination = dao.find(userId, open, pagingInfo);
        assertEquals(0L, postPagination.getStartPostId());
        assertEquals(20, postPagination.getOffset());
        assertTrue(postPagination.isStartInclude());
        assertTrue(postPagination.emptyInIdList());

    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenInCache_AndNeededIndexs0_AndASC() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(6, 45, PostSort.newest);

        when(cache.get(userId, SizeInfo.Open, true, 270))
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPagination(
                                        PostPagination.builder()
                                                .startPostId(999L)
                                                .offset(10)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededIndexs(0)
                                .build()
                );

        PostPagination postPagination = dao.find(userId, open, pagingInfo);

        assertEquals(999L, postPagination.getStartPostId());
        assertEquals(10, postPagination.getOffset());
        assertFalse(postPagination.isStartInclude());
        assertTrue(postPagination.emptyInIdList());

    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenInCache_AndNeededIndexs6_AndASC() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> postEntitys = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true, false, 300, 1);

        em.flush();
        em.clear();

        Identity userId = Identity.from(userEntity.getId());
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(10, 20, PostSort.newest);

        List<Long> idList = postEntitys.keySet().stream()
                .sorted(Long::compareTo)
                .collect(Collectors.toList());


        when(cache.get(userId, SizeInfo.Open, true, 200)) // want index 9 // get index 3
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPagination(
                                        PostPagination.builder()
                                                .startPostId(idList.get((3 + 1) * 20 - 1))
                                                .offset(120)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededIndexs(6)
                                .countPerIndex(20)
                                .build()
                );


        PostPagination postPagination = dao.find(userId, open, pagingInfo);

        assertEquals(idList.get(200), postPagination.getStartPostId());
        assertEquals(0, postPagination.getOffset());
        assertTrue(postPagination.isStartInclude());
        assertFalse(postPagination.emptyInIdList());
        assertFalse(postPagination.isNoResult());
        assertEquals(idList.stream().skip(200).limit(20).collect(Collectors.toList()), postPagination.getInIdList());

        long existingOffset = 200;
        long realOffset = 120 - (20 * 3);
        long cacheOffset = 120;
        verify(cache, times(1))
                .get(userId, SizeInfo.Open, true, 200);

        verify(cache, times(1))
                .put(eq(userId), eq(SizeInfo.Open), eq(true), eq(realOffset + existingOffset - cacheOffset),
                        eq(idList.stream().skip(200 - (20 * 3)).limit(pagingInfo.getSize() + (20 * 3 * 2)).collect(Collectors.toList()))
                );
    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenNotInCache_AndNeededIndexs2_AndASC() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> postEntitys = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true, false, 100, 1);

        em.flush();
        em.clear();

        Identity userId = Identity.from(userEntity.getId());
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(2, 25, PostSort.newest);

        List<Long> idList = postEntitys.keySet().stream()
                .sorted(Long::compareTo)
                .collect(Collectors.toList());


        when(cache.get(userId, SizeInfo.Open, true, 50))
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPagination(
                                        PostPagination.builder()
                                                .startPostId(0L)
                                                .offset(50)
                                                .startInclude(true)
                                                .build()
                                )
                                .neededIndexs(2)
                                .countPerIndex(20)
                                .build()
                );


        PostPagination postPagination = dao.find(userId, open, pagingInfo);

        assertEquals(0L, postPagination.getStartPostId());
        assertEquals(50, postPagination.getOffset());
        assertTrue(postPagination.isStartInclude());
        assertTrue(postPagination.emptyInIdList());


        verify(cache, times(1))
                .get(userId, SizeInfo.Open, true, 50);
    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenInCache_AndNeededIndexs0_AndDESC() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> postEntitys = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true, false, 200, 1);

        em.flush();
        em.clear();

        Identity userId = Identity.from(userEntity.getId());
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(2, 25, PostSort.oldest);

        List<Long> idList = postEntitys.keySet().stream()
                .sorted((a, b) -> -1 * a.compareTo(b))
                .collect(Collectors.toList());


        when(cache.get(userId, SizeInfo.Open, false, 50)) // want index 1 -> get index 1
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPagination(
                                        PostPagination.builder()
                                                .startPostId(idList.get((1 + 1) * 20 - 1))
                                                .offset(10)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededIndexs(0)
                                .build()
                );


        PostPagination postPagination = dao.find(userId, open, pagingInfo);
        
        assertEquals(idList.get(40 - 1), postPagination.getStartPostId());
        assertEquals(10, postPagination.getOffset());
        assertFalse(postPagination.isStartInclude());

        verify(cache, times(1))
                .get(userId, SizeInfo.Open, false, 50);
    }


    @Test
    void find_ShouldReturnPostPaginationInfo_WhenInCache_AndNeededIndexs4_AndDESC() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> postEntitys = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true, false, 400, 1);

        em.flush();
        em.clear();

        Identity userId = Identity.from(userEntity.getId());
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(9, 25, PostSort.oldest);

        List<Long> idList = postEntitys.keySet().stream()
                .sorted((a, b) -> -1 * a.compareTo(b))
                .collect(Collectors.toList());

        when(cache.get(userId, SizeInfo.Open, false, 225)) // want index 10 // get index 6
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPagination(
                                        PostPagination.builder()
                                                .startPostId(idList.get((6 + 1) * 20 - 1))
                                                .offset(85)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededIndexs(4)
                                .countPerIndex(20)
                                .build()
                );

        PostPagination postPagination = dao.find(userId, open, pagingInfo);

        assertEquals(idList.get(225), postPagination.getStartPostId());
        assertEquals(0, postPagination.getOffset());
        assertTrue(postPagination.isStartInclude());
        assertFalse(postPagination.emptyInIdList());
        assertFalse(postPagination.isNoResult());
        assertEquals(idList.stream().skip(225).limit(25).collect(Collectors.toList()), postPagination.getInIdList());

        long existingOffset = 225;
        long realOffset = 85 - (20 * 3);
        long cacheOffset = 85;
        verify(cache, times(1))
                .get(userId, SizeInfo.Open, false, 225);

        verify(cache, times(1))
                .put(eq(userId), eq(SizeInfo.Open), eq(false), eq(realOffset + existingOffset - cacheOffset),
                        eq(idList.stream().skip(225 - (20 * 3)).limit(pagingInfo.getSize() + (20 * 3 * 2)).collect(Collectors.toList()))
                );
    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenNotInCache_NonExistentSelectResults_ASC() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> postEntitys = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true, false, 70, 1);

        em.flush();
        em.clear();

        Identity userId = Identity.from(userEntity.getId());
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(4, 20, PostSort.newest);

        List<Long> idList = postEntitys.keySet().stream()
                .sorted(Long::compareTo)
                .collect(Collectors.toList());

        when(cache.get(userId, SizeInfo.Open, true, 80)) // want index 3 // get index null
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPagination(
                                        PostPagination.builder()
                                                .startPostId(0L)
                                                .offset(80)
                                                .startInclude(true)
                                                .build()
                                )
                                .neededIndexs(4)
                                .countPerIndex(20)
                                .build()
                );


        PostPagination postPagination = dao.find(userId, open, pagingInfo);

        assertEquals(0L, postPagination.getStartPostId());
        assertEquals(80, postPagination.getOffset());
        assertTrue(postPagination.isStartInclude());
        assertTrue(postPagination.emptyInIdList());
        assertTrue(postPagination.isNoResult());
    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenNotInCache_NonExistentSelectResults_DESC() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> postEntitys = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true, false, 70, 1);

        em.flush();
        em.clear();

        Identity userId = Identity.from(userEntity.getId());
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(4, 20, PostSort.oldest);

        List<Long> idList = postEntitys.keySet().stream()
                .sorted((a, b) -> -1 * a.compareTo(b))
                .collect(Collectors.toList());

        when(cache.get(userId, SizeInfo.Open, false, 80)) // want index 3 // get index null
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPagination(
                                        PostPagination.builder()
                                                .startPostId(Long.MAX_VALUE)
                                                .offset(80)
                                                .startInclude(true)
                                                .build()
                                )
                                .neededIndexs(4)
                                .countPerIndex(20)
                                .build()
                );


        PostPagination postPagination = dao.find(userId, open, pagingInfo);

        assertEquals(Long.MAX_VALUE, postPagination.getStartPostId());
        assertEquals(80, postPagination.getOffset());
        assertTrue(postPagination.isStartInclude());
        assertTrue(postPagination.emptyInIdList());
        assertTrue(postPagination.isNoResult());
    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenNotInCache_LessSelectResults() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> postEntitys = TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true, false, 150, 1);

        em.flush();
        em.clear();

        Identity userId = Identity.from(userEntity.getId());
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(7, 20, PostSort.newest); // 141 ~ 160

        List<Long> idList = postEntitys.keySet().stream()
                .sorted(Long::compareTo)
                .collect(Collectors.toList());

        when(cache.get(userId, SizeInfo.Open, true, 140)) // want index 6 // get index 3
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPagination(
                                        PostPagination.builder()
                                                .startPostId(idList.get((3 + 1) * 20 - 1))
                                                .offset(60)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededIndexs(3)
                                .countPerIndex(20)
                                .build()
                );

        PostPagination postPagination = dao.find(userId, open, pagingInfo);

        assertEquals(idList.get(140), postPagination.getStartPostId());
        assertEquals(0, postPagination.getOffset());
        assertTrue(postPagination.isStartInclude());
        assertFalse(postPagination.emptyInIdList());
        assertFalse(postPagination.isNoResult());
        assertEquals(idList.stream().skip(140).limit(10).collect(Collectors.toList()), postPagination.getInIdList());
    }
}