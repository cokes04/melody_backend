package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class PostDetailRepositoryImplTest {
    private PostDetailRepositoryImpl repository;
    private PostDetailDao dao;
    private PostTotalSizeCache totalSizeCache;

    @BeforeEach
    void setUp() {
        dao = Mockito.mock(PostDetailDao.class);
        totalSizeCache = Mockito.mock(PostTotalSizeCache.class);
        repository = new PostDetailRepositoryImpl(dao, totalSizeCache);

    }

    @Test
    void findById_ShouldRunDao() {
        Identity postId = TestPostDomainGenerator.randomPostId();

        repository.findById(postId);

        verify(dao, times(1))
                .findById(postId);
        verifyNoInteractions(totalSizeCache);
    }

    @Test
    void findByUserId_ShouldGetTotalSizeInCache_WhenEveryting_EverytingTotalSizeInCache() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.Everything;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(totalSizeCache.getTotalSize(userId, Open.Everything))
                .thenReturn(Optional.of(777L));

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(777L, result.getTotalCount());
        assertEquals(78, result.getTotalPage());

        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.Everything);
        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.OnlyOpen);
        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.OnlyClose);

        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), any(Open.class), anyLong());
        verify(dao, times(0)).findTotalSizeByUserId(eq(userId), any(Open.class));
    }

    @Test
    void findByUserId_ShouldRunDaoAndUpdateCache_WhenEveryting_EmptyCache() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.Everything;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(dao.findTotalSizeByUserId(userId, Open.OnlyOpen))
                .thenReturn(444L);

        when(dao.findTotalSizeByUserId(userId, Open.OnlyClose))
                .thenReturn(222L);

        when(totalSizeCache.getTotalSize(eq(userId), any(Open.class)))
                .thenReturn(Optional.empty());

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(666L, result.getTotalCount());
        assertEquals(67, result.getTotalPage());

        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.Everything);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyOpen);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyClose);

        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), eq(Open.Everything), anyLong());
        verify(totalSizeCache, times(1)).putTotalSize(eq(userId), eq(Open.OnlyOpen), anyLong());
        verify(totalSizeCache, times(1)).putTotalSize(eq(userId), eq(Open.OnlyClose), anyLong());

        verify(dao, times(0)).findTotalSizeByUserId(userId, Open.Everything);
        verify(dao, times(1)).findTotalSizeByUserId(userId, Open.OnlyOpen);
        verify(dao, times(1)).findTotalSizeByUserId(userId, Open.OnlyClose);
    }

    @Test
    void findByUserId_ShouldRunDaoAndUpdateCache_WhenEveryting_OnlyOpenTotalSizeInCache() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.Everything;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(totalSizeCache.getTotalSize(userId, Open.Everything))
                .thenReturn(Optional.empty());

        when(totalSizeCache.getTotalSize(userId, Open.OnlyOpen))
                .thenReturn(Optional.of(111L));

        when(totalSizeCache.getTotalSize(userId, Open.OnlyClose))
                .thenReturn(Optional.empty());

        when(dao.findTotalSizeByUserId(userId, Open.OnlyClose))
                .thenReturn(222L);

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(333L, result.getTotalCount());
        assertEquals(34, result.getTotalPage());

        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.Everything);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyOpen);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyClose);

        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), eq(Open.Everything), anyLong());
        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), eq(Open.OnlyOpen), anyLong());
        verify(totalSizeCache, times(1)).putTotalSize(eq(userId), eq(Open.OnlyClose), anyLong());

        verify(dao, times(0)).findTotalSizeByUserId(userId, Open.Everything);
        verify(dao, times(0)).findTotalSizeByUserId(userId, Open.OnlyOpen);
        verify(dao, times(1)).findTotalSizeByUserId(userId, Open.OnlyClose);
    }

    @Test
    void findByUserId_ShouldRunDaoAndUpdateCache_WhenEveryting_OnlyCloseTotalSizeInCache() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.Everything;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(totalSizeCache.getTotalSize(userId, Open.Everything))
                .thenReturn(Optional.empty());

        when(totalSizeCache.getTotalSize(userId, Open.OnlyOpen))
                .thenReturn(Optional.empty());

        when(totalSizeCache.getTotalSize(userId, Open.OnlyClose))
                .thenReturn(Optional.of(111L));

        when(dao.findTotalSizeByUserId(userId, Open.OnlyOpen))
                .thenReturn(222L);

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(333L, result.getTotalCount());
        assertEquals(34, result.getTotalPage());

        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.Everything);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyOpen);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyClose);

        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), eq(Open.Everything), anyLong());
        verify(totalSizeCache, times(1)).putTotalSize(eq(userId), eq(Open.OnlyOpen), anyLong());
        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), eq(Open.OnlyClose), anyLong());

        verify(dao, times(0)).findTotalSizeByUserId(userId, Open.Everything);
        verify(dao, times(1)).findTotalSizeByUserId(userId, Open.OnlyOpen);
        verify(dao, times(0)).findTotalSizeByUserId(userId, Open.OnlyClose);
    }

    @Test
    void findByUserId_ShouldGetTotalSizeInCache_WhenOnlyClose_OnlyCloseTotalSizeInCache() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyClose;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(totalSizeCache.getTotalSize(userId, Open.OnlyClose))
                .thenReturn(Optional.of(111L));

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(111L, result.getTotalCount());
        assertEquals(12, result.getTotalPage());

        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.Everything);
        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.OnlyOpen);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyClose);

        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), any(Open.class), anyLong());
        verify(dao, times(0)).findTotalSizeByUserId(eq(userId), any(Open.class));
    }


    @Test
    void findByUserId_ShouldRunDaoAndUpdateCache_WhenOnlyClose_OnlyCloseTotalSizeNotInCache() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyClose;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(totalSizeCache.getTotalSize(userId, Open.OnlyClose))
                .thenReturn(Optional.empty());

        when(dao.findTotalSizeByUserId(userId, Open.OnlyClose))
                .thenReturn(111L);

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(111L, result.getTotalCount());
        assertEquals(12, result.getTotalPage());

        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.Everything);
        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.OnlyOpen);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyClose);

        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), eq(Open.Everything), anyLong());
        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), eq(Open.OnlyOpen), anyLong());
        verify(totalSizeCache, times(1)).putTotalSize(eq(userId), eq(Open.OnlyClose), anyLong());

        verify(dao, times(0)).findTotalSizeByUserId(userId, Open.Everything);
        verify(dao, times(0)).findTotalSizeByUserId(userId, Open.OnlyOpen);
        verify(dao, times(1)).findTotalSizeByUserId(userId, Open.OnlyClose);
    }

    @Test
    void findByUserId_ShouldGetTotalSizeInCache_WhenOnlyOpen_OnlyOpenTotalSizeInCache() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(totalSizeCache.getTotalSize(userId, Open.OnlyOpen))
                .thenReturn(Optional.of(111L));

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(111L, result.getTotalCount());
        assertEquals(12, result.getTotalPage());

        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.Everything);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyOpen);
        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.OnlyClose);

        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), any(Open.class), anyLong());
        verify(dao, times(0)).findTotalSizeByUserId(eq(userId), any(Open.class));
    }

    @Test
    void findByUserId_ShouldRunDaoAndUpdateCache_WhenOnlyOpen_OnlyOpenTotalSizeNotInCache() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(totalSizeCache.getTotalSize(userId, Open.OnlyOpen))
                .thenReturn(Optional.empty());

        when(dao.findTotalSizeByUserId(userId, Open.OnlyOpen))
                .thenReturn(111L);

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(111L, result.getTotalCount());
        assertEquals(12, result.getTotalPage());

        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.Everything);
        verify(totalSizeCache, times(1)).getTotalSize(userId, Open.OnlyOpen);
        verify(totalSizeCache, times(0)).getTotalSize(userId, Open.OnlyClose);

        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), eq(Open.Everything), anyLong());
        verify(totalSizeCache, times(1)).putTotalSize(eq(userId), eq(Open.OnlyOpen), anyLong());
        verify(totalSizeCache, times(0)).putTotalSize(eq(userId), eq(Open.OnlyClose), anyLong());

        verify(dao, times(0)).findTotalSizeByUserId(userId, Open.Everything);
        verify(dao, times(1)).findTotalSizeByUserId(userId, Open.OnlyOpen);
        verify(dao, times(0)).findTotalSizeByUserId(userId, Open.OnlyClose);
    }
}
