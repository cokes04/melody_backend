package com.melody.melody.adapter.persistence.post.postPagination;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(PersistenceTestConfig.class)
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

        PostPaginationInfo postPaginationInfo = dao.find(userId, open, pagingInfo);
        assertNull(postPaginationInfo.getStartPostId());
        assertEquals(0, postPaginationInfo.getOffset());
        assertFalse(postPaginationInfo.isStartInclude());

    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenInCache_AndNeededPages0_AndASC() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(6, 45, PostSort.newest);

        when(cache.get(userId, SizeInfo.Open, true, 270))
                .thenReturn(
                        UserPostPaginationCache.Result.builder()
                                .postPaginationInfo(
                                        PostPaginationInfo.builder()
                                                .startPostId(999L)
                                                .offset(10)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededPages(0)
                                .build()
                );

        PostPaginationInfo postPaginationInfo = dao.find(userId, open, pagingInfo);

        assertEquals(999L, postPaginationInfo.getStartPostId());
        assertEquals(10, postPaginationInfo.getOffset());
        assertFalse(postPaginationInfo.isStartInclude());

    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenInCache_AndNeededPages1_AndASC() {
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
                                .postPaginationInfo(
                                        PostPaginationInfo.builder()
                                                .startPostId(idList.get((0 + 1) * 20 - 1))
                                                .offset(30)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededPages(1)
                                .build()
                );


        PostPaginationInfo postPaginationInfo = dao.find(userId, open, pagingInfo);

        assertEquals(idList.get(50), postPaginationInfo.getStartPostId());
        assertEquals(0, postPaginationInfo.getOffset());
        assertTrue(postPaginationInfo.isStartInclude());

        verify(cache, times(1))
                .get(userId, SizeInfo.Open, true, 50);

        verify(cache, times(1))
                .put(eq(userId), eq(SizeInfo.Open), eq(true), eq(50L - 30L),
                        eq(idList.stream().skip(50 - 30).limit(30 + 1).collect(Collectors.toList()))
                );
    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenNotInCache_AndASC() {
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
                                .postPaginationInfo(
                                        PostPaginationInfo.builder()
                                                .startPostId(null)
                                                .offset(50)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededPages(2)
                                .build()
                );


        PostPaginationInfo postPaginationInfo = dao.find(userId, open, pagingInfo);

        assertEquals(idList.get(50), postPaginationInfo.getStartPostId());
        assertEquals(0, postPaginationInfo.getOffset());
        assertTrue(postPaginationInfo.isStartInclude());

        verify(cache, times(1))
                .get(userId, SizeInfo.Open, true, 50);

        verify(cache, times(1))
                .put(eq(userId), eq(SizeInfo.Open), eq(true), eq(0L),
                        eq(idList.stream().limit(51).collect(Collectors.toList()))
                );
    }

    @Test
    void find_ShouldReturnPostPaginationInfo_WhenInCache_AndNeededPages0_AndDESC() {
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
                                .postPaginationInfo(
                                        PostPaginationInfo.builder()
                                                .startPostId(idList.get((1 + 1) * 20 - 1))
                                                .offset(10)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededPages(0)
                                .build()
                );


        PostPaginationInfo postPaginationInfo = dao.find(userId, open, pagingInfo);
        
        assertEquals(idList.get(40 - 1), postPaginationInfo.getStartPostId());
        assertEquals(10, postPaginationInfo.getOffset());
        assertFalse(postPaginationInfo.isStartInclude());

        verify(cache, times(1))
                .get(userId, SizeInfo.Open, false, 50);
    }


    @Test
    void find_ShouldReturnPostPaginationInfo_WhenInCache_AndNeededPages4_AndDESC() {
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
                                .postPaginationInfo(
                                        PostPaginationInfo.builder()
                                                .startPostId(idList.get((6 + 1) * 20 - 1))
                                                .offset(85)
                                                .startInclude(false)
                                                .build()
                                )
                                .neededPages(4)
                                .build()
                );

        PostPaginationInfo postPaginationInfo = dao.find(userId, open, pagingInfo);

        assertEquals(idList.get(225), postPaginationInfo.getStartPostId());
        assertEquals(0, postPaginationInfo.getOffset());
        assertTrue(postPaginationInfo.isStartInclude());

        verify(cache, times(1))
                .get(userId, SizeInfo.Open, false, 225);

        verify(cache, times(1))
                .put(eq(userId), eq(SizeInfo.Open), eq(false), eq(225L - 85L),
                        eq(idList.stream().skip(225 - 85).limit(85 + 1).collect(Collectors.toList()))
                );
    }
}