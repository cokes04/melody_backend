package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.adapter.persistence.post.size.PostSizeDao;
import com.melody.melody.adapter.persistence.post.size.SizeInfo;
import com.melody.melody.adapter.persistence.post.pagination.PostPaginationDao;
import com.melody.melody.adapter.persistence.post.pagination.PostPagination;
import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PostDetailRepositoryImplTest {
    private PostDetailRepositoryImpl repository;
    private PostDetailDao detailDao;
    private PostPaginationDao postPaginationDao;
    private PostSizeDao sizeDao;

    @BeforeEach
    void setUp() {
        detailDao = Mockito.mock(PostDetailDao.class);
        sizeDao = Mockito.mock(PostSizeDao.class);
        postPaginationDao = Mockito.mock(PostPaginationDao.class);
        repository = new PostDetailRepositoryImpl(detailDao, postPaginationDao, sizeDao);

    }

    @Test
    void findById_ShouldRunDao() {
        Identity postId = TestPostDomainGenerator.randomPostId();

        repository.findById(postId);

        verify(detailDao, times(1))
                .findById(postId);
    }

    @Test
    void findByUserId_ShouldReturnOpenAndCloseSize_WhenEveryting() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.Everything;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(sizeDao.findSize(userId, SizeInfo.Open))
                .thenReturn(444L);

        when(sizeDao.findSize(userId, SizeInfo.Close))
                .thenReturn(222L);

        when(postPaginationDao.find(userId, open, pagingInfo))
                .thenReturn(emptyPostPaginationInfo());

        when(detailDao.findByUserId(eq(userId), eq(open), anyLong(), anyBoolean(), anyLong(), anyInt(), any(PostSort.class)))
                .thenReturn(new ArrayList<>());

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(666L, result.getTotalCount());
        assertEquals(67, result.getTotalPage());

        verify(sizeDao, times(1)).findSize(userId, SizeInfo.Open);
        verify(sizeDao, times(1)).findSize(userId, SizeInfo.Close);
    }

    @Test
    void findByUserId_ShouldReturnCloseSize_WhenOnlyClose() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyClose;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(sizeDao.findSize(userId, SizeInfo.Close))
                .thenReturn(111L);

        when(postPaginationDao.find(userId, open, pagingInfo))
                .thenReturn(emptyPostPaginationInfo());

        when(detailDao.findByUserId(eq(userId), eq(open), anyLong(), anyBoolean(), anyLong(), anyInt(), any(PostSort.class)))
                .thenReturn(new ArrayList<>());


        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(111L, result.getTotalCount());
        assertEquals(12, result.getTotalPage());

        verify(sizeDao, times(0)).findSize(userId, SizeInfo.Open);
        verify(sizeDao, times(1)).findSize(userId, SizeInfo.Close);
    }

    @Test
    void findByUserId_ShouldReturnOpenSize_WhenOnlyOpen() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(sizeDao.findSize(userId, SizeInfo.Open))
                .thenReturn(111L);

        when(postPaginationDao.find(userId, open, pagingInfo))
                .thenReturn(emptyPostPaginationInfo());

        when(detailDao.findByUserId(eq(userId), eq(open), anyLong(), anyBoolean(), anyLong(), anyInt(), any(PostSort.class)))
                .thenReturn(new ArrayList<>());

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(111L, result.getTotalCount());
        assertEquals(12, result.getTotalPage());

        verify(sizeDao, times(1)).findSize(userId, SizeInfo.Open);
        verify(sizeDao, times(0)).findSize(userId, SizeInfo.Close);
    }


    private PostPagination emptyPostPaginationInfo(){
        return PostPagination.builder()
                .build();
    }
}
