package com.melody.melody.adapter.persistence.post.pagination;

import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPostPaginationCachceEvictAspectTest {
    private UserPostPaginationCachceEvictAspect aspect;
    private UserPostPaginationCache cache;
    private JoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        joinPoint = Mockito.mock(JoinPoint.class);
        cache = Mockito.mock(UserPostPaginationCache.class);
        aspect = new UserPostPaginationCachceEvictAspect(cache);
    }

    @Test
    void evictWhenSave_ShouldRunEvictUserDESCPostPagination_WhenCreatePost() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Post post = TestPostDomainGenerator.randomOpenPost(Identity.empty(), userId);

        when(joinPoint.getArgs())
                .thenReturn(new Object[]{post});

        aspect.evictWhenSave(joinPoint);

        verify(cache, times(1))
                .evict(userId, true);
    }

    @Test
    void evictWhenSave_ShouldRunEvictUserAfterPostIdPostPagination_WhenNotCreatePost() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Post post = TestPostDomainGenerator.randomOpenPost(userId);

        when(joinPoint.getArgs())
                .thenReturn(new Object[]{post});

        aspect.evictWhenSave(joinPoint);

        verify(cache, times(1))
                .evict(userId, post.getId());
    }

    @Test
    void evictWhenDeleteByUserId_ShouldRunEvictUserPostPagination() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        when(joinPoint.getArgs())
                .thenReturn(new Object[]{userId});

        aspect.evictWhenDeleteByUserId(joinPoint);

        verify(cache, times(1))
                .evict(userId);
    }
}