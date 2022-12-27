package com.melody.melody.adapter.cache.post;

import com.melody.melody.application.dto.Open;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostTotalSizeCacheImplTest {

    private PostTotalSizeCacheImpl cacheRepository;
    private UserPostTotalCache cache;

    @BeforeEach
    void setUp() {
        cache = Mockito.mock(UserPostTotalCache.class);

        cacheRepository = new PostTotalSizeCacheImpl(cache);
    }

    @Test
    void getTotalSize_ShouldReturnTotalSize_WhenEveryting_HaveCloseAndOpen() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        when(cache.get(userId.getValue(), CountInfo.Open))
                .thenReturn(300L);

        when(cache.get(userId.getValue(), CountInfo.Close))
                .thenReturn(500L);

        Optional<Long> actual = cacheRepository.getTotalSize(userId, Open.Everything);
        assertTrue(actual.isPresent());
        assertEquals(800L, actual.get());
    }

    @Test
    void get_ShouldReturnNull_WhenEveryting_NotHaveClose() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        when(cache.get(userId.getValue(), CountInfo.Open))
                .thenReturn(100L);

        when(cache.get(userId.getValue(), CountInfo.Close))
                .thenReturn(null);

        Optional<Long> actual = cacheRepository.getTotalSize(userId, Open.Everything);
        assertTrue(actual.isEmpty());
    }

    @Test
    void get_ShouldReturnNull_WhenEveryting_NotHaveOpenKey() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        when(cache.get(userId.getValue(), CountInfo.Open))
                .thenReturn(null);

        when(cache.get(userId.getValue(), CountInfo.Close))
                .thenReturn(500L);

        Optional<Long> actual = cacheRepository.getTotalSize(userId, Open.Everything);
        assertTrue(actual.isEmpty());
    }


    @Test
    void getTotalSize_ShouldReturnTotalSize_WhenOnlyOpen_HaveOpen() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        when(cache.get(userId.getValue(), CountInfo.Open))
                .thenReturn(300L);

        Optional<Long> actual = cacheRepository.getTotalSize(userId, Open.OnlyOpen);
        assertTrue(actual.isPresent());
        assertEquals(300L, actual.get());
    }

    @Test
    void getTotalSize_ShouldReturnEmpty_WhenOnlyOpen_NotHaveOpen() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        when(cache.get(userId.getValue(), CountInfo.Open))
                .thenReturn(null);

        Optional<Long> actual = cacheRepository.getTotalSize(userId, Open.OnlyOpen);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getTotalSize_ShouldReturnTotalSize_WhenOnlyClose_HaveClose() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        when(cache.get(userId.getValue(), CountInfo.Close))
                .thenReturn(300L);

        Optional<Long> actual = cacheRepository.getTotalSize(userId, Open.OnlyClose);
        assertTrue(actual.isPresent());
        assertEquals(300L, actual.get());
    }

    @Test
    void getTotalSize_ShouldReturnEmpty_WhenOnlyClose_NotHaveClose() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        when(cache.get(userId.getValue(), CountInfo.Close))
                .thenReturn(null);

        Optional<Long> actual = cacheRepository.getTotalSize(userId, Open.OnlyClose);
        assertTrue(actual.isEmpty());
    }

    @Test
    void putTotalSize_ShouldNotPut_WhenEveryting() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        cacheRepository.putTotalSize(userId, Open.Everything, 2031L);

        verify(cache, times(0)).put(anyLong(), any(CountInfo.class), anyLong());
    }

    @Test
    void putTotalSize_ShouldPut_WhenOnlyOpen() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        cacheRepository.putTotalSize(userId, Open.OnlyOpen, 2031L);

        verify(cache, times(1)).put(userId.getValue(), CountInfo.Open, 2031L);
    }

    @Test
    void putTotalSize_ShouldPut_WhenOnlyClose() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        cacheRepository.putTotalSize(userId, Open.OnlyClose, 2031L);

        verify(cache, times(1)).put(userId.getValue(), CountInfo.Close, 2031L);

    }

    @Test
    void putDeletedTotalSize_ShouldPut() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        cacheRepository.putDeletedTotalSize(userId, 2031L);

        verify(cache, times(1)).put(userId.getValue(), CountInfo.Deleted, 2031L);

    }
}