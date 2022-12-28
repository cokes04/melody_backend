package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.adapter.persistence.post.PostDao;
import com.melody.melody.adapter.persistence.post.SizeInfo;
import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PostDetailRepositoryImplTest {
    private PostDetailRepositoryImpl repository;
    private PostDetailDao detailDao;
    private PostDao postDao;

    @BeforeEach
    void setUp() {
        detailDao = Mockito.mock(PostDetailDao.class);
        postDao = Mockito.mock(PostDao.class);
        repository = new PostDetailRepositoryImpl(detailDao, postDao);

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

        when(postDao.findSize(userId, Open.OnlyOpen, SizeInfo.Open))
                .thenReturn(444L);

        when(postDao.findSize(userId, Open.OnlyClose, SizeInfo.Close))
                .thenReturn(222L);

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(666L, result.getTotalCount());
        assertEquals(67, result.getTotalPage());

        verify(postDao, times(1)).findSize(userId, Open.OnlyOpen, SizeInfo.Open);
        verify(postDao, times(1)).findSize(userId, Open.OnlyClose, SizeInfo.Close);
    }

    @Test
    void findByUserId_ShouldReturnCloseSize_WhenOnlyClose() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyClose;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(postDao.findSize(userId, Open.OnlyClose, SizeInfo.Close))
                .thenReturn(111L);

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(111L, result.getTotalCount());
        assertEquals(12, result.getTotalPage());

        verify(postDao, times(0)).findSize(userId, Open.OnlyOpen, SizeInfo.Open);
        verify(postDao, times(1)).findSize(userId, Open.OnlyClose, SizeInfo.Close);
    }

    @Test
    void findByUserId_ShouldReturnOpenSize_WhenOnlyOpen() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.OnlyOpen;
        PagingInfo<PostSort> pagingInfo = new PagingInfo<PostSort>(0, 10, PostSort.newest);

        when(postDao.findSize(userId, Open.OnlyOpen, SizeInfo.Open))
                .thenReturn(111L);

        PagingResult<PostDetail> result = repository.findByUserId(userId, open, pagingInfo);
        assertEquals(111L, result.getTotalCount());
        assertEquals(12, result.getTotalPage());

        verify(postDao, times(1)).findSize(userId, Open.OnlyOpen, SizeInfo.Open);
        verify(postDao, times(0)).findSize(userId, Open.OnlyClose, SizeInfo.Close);
    }
}
