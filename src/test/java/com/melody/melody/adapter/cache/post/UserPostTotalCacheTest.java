package com.melody.melody.adapter.cache.post;

import com.melody.melody.adapter.cache.CacheType;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserPostTotalCacheTest {
    private UserPostTotalCache totalPageInfoCache;
    private CacheManager cacheManager;
    private Cache cache;

    @BeforeEach
    void setUp() {
        cacheManager = Mockito.mock(CacheManager.class);
        cache = Mockito.mock(Cache.class);

        totalPageInfoCache = new UserPostTotalCache(cacheManager);

        when(cacheManager.getCache(CacheType.UserPostTotal.getCacheName()))
                .thenReturn(cache);
    }

    @Test
    void get_ShouldReturnValue_WhenHaveKey() {
        long userId = TestUserEntityGenerator.randomId();
        long value = 400L;

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(value);

        Long actual = totalPageInfoCache.get(userId, CountInfo.Open);
        assertNotNull(actual);
        assertEquals(value, actual);
    }

    @Test
    void get_ShouldReturnNull_WhenNotHaveKey() {
        long userId = TestUserEntityGenerator.randomId();

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(null);

        Long actual = totalPageInfoCache.get(userId, CountInfo.Open);
        assertNull(actual);
    }


    @Test
    void put_ShouldPutValue() {
        long userId = TestUserEntityGenerator.randomId();

        totalPageInfoCache.put(userId, CountInfo.Open, 1231023L);

        verify(cacheManager, times(1)).getCache(anyString());
        verify(cache, times(1)).put(anyString(), eq(1231023L));
    }

    @Test
    void increase_ShouldIncreased_WhenOpen_OpenInCache() {
        long userId = TestUserEntityGenerator.randomId();

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(30L);

        totalPageInfoCache.increase(userId, CountInfo.Open);

        verify(cache, times(1)).get(anyString(), eq(Long.class));
        verify(cache, times(1)).put(anyString(), eq(31L));
    }

    @Test
    void increase_ShouldIncreased_WhenOpen_OpenNotInCache() {
        long userId = TestUserEntityGenerator.randomId();

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(null);

        totalPageInfoCache.increase(userId, CountInfo.Open);

        verify(cache, times(1)).get(anyString(), eq(Long.class));
        verify(cache, times(0)).put(anyString(), anyLong());
    }

    @Test
    void increase_ShouldIncreased_WhenClose_CloseInCache() {
        long userId = TestUserEntityGenerator.randomId();

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(30L);

        totalPageInfoCache.increase(userId, CountInfo.Close);

        verify(cache, times(1)).get(anyString(), eq(Long.class));
        verify(cache, times(1)).put(anyString(), eq(31L));
    }

    @Test
    void increase_ShouldNotIncreased_WhenClose_CloseNotInCache() {
        long userId = TestUserEntityGenerator.randomId();

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(null);

        totalPageInfoCache.increase(userId, CountInfo.Close);

        verify(cache, times(1)).get(anyString(), eq(Long.class));
        verify(cache, times(0)).put(anyString(), anyLong());

    }

    @Test
    void decrease_ShouldDecrease_WhenOpen_OpenInCache() {
        long userId = TestUserEntityGenerator.randomId();

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(30L);

        totalPageInfoCache.decrease(userId, CountInfo.Open);

        verify(cache, times(1)).get(anyString(), eq(Long.class));
        verify(cache, times(1)).put(anyString(), eq(29L));
    }

    @Test
    void decrease_ShouldDecrease_WhenOpen_OpenNotInCache() {
        long userId = TestUserEntityGenerator.randomId();

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(null);

        totalPageInfoCache.decrease(userId, CountInfo.Open);

        verify(cache, times(1)).get(anyString(), eq(Long.class));
        verify(cache, times(0)).put(anyString(), anyLong());
    }

    @Test
    void decrease_ShouldDecrease_WhenClose_CloseInCache() {
        long userId = TestUserEntityGenerator.randomId();

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(30L);

        totalPageInfoCache.decrease(userId, CountInfo.Close);

        verify(cache, times(1)).get(anyString(), eq(Long.class));
        verify(cache, times(1)).put(anyString(), eq(29L));
    }

    @Test
    void decrease_ShouldNotDecrease_WhenClose_CloseNotInCache() {
        long userId = TestUserEntityGenerator.randomId();

        when(cache.get(anyString(), eq(Long.class)))
                .thenReturn(null);

        totalPageInfoCache.decrease(userId, CountInfo.Close);

        verify(cache, times(1)).get(anyString(), eq(Long.class));
        verify(cache, times(0)).put(anyString(), anyLong());

    }
    @Test
    void update_ShouldUpAfter_WhenNullPrevious() {
        long userId = TestUserEntityGenerator.randomId();

        String afterKey = getKey(userId, CountInfo.Close);
        when(cache.get(eq(afterKey), eq(Long.class)))
                .thenReturn(100L);

        totalPageInfoCache.update(userId, null, CountInfo.Close);

        verify(cache, times(1)).get(eq(afterKey), eq(Long.class));
        verify(cache, times(1)).put(eq(afterKey), eq(101L));
        verify(cache, times(1)).put(anyString(), anyLong());
    }

    @Test
    void update_ShouldDownPrevious_WhenNullAfter() {
        long userId = TestUserEntityGenerator.randomId();

        String previousKey = getKey(userId, CountInfo.Close);
        when(cache.get(eq(previousKey), eq(Long.class)))
                .thenReturn(100L);

        totalPageInfoCache.update(userId, CountInfo.Close, null);

        verify(cache, times(1)).get(eq(previousKey), eq(Long.class));
        verify(cache, times(1)).put(eq(previousKey), eq(99L));
        verify(cache, times(1)).put(anyString(), anyLong());
    }

    @Test
    void update_ShouldDownPreviousAndUpAfter_WhenNotNullPreviousAndAfter() {
        long userId = TestUserEntityGenerator.randomId();

        String previousKey = getKey(userId, CountInfo.Open);
        String afterKey = getKey(userId, CountInfo.Deleted);

        when(cache.get(eq(previousKey), eq(Long.class)))
                .thenReturn(100L);

        when(cache.get(eq(afterKey), eq(Long.class)))
                .thenReturn(500L);

        totalPageInfoCache.update(userId, CountInfo.Open, CountInfo.Deleted);

        verify(cache, times(1)).get(eq(previousKey), eq(Long.class));
        verify(cache, times(1)).get(eq(afterKey), eq(Long.class));
        verify(cache, times(1)).put(eq(previousKey), eq(99L));
        verify(cache, times(1)).put(eq(afterKey), eq(501L));
    }


    @Test
    void removeUser_ShouldRemovedUserEveryInfo() {
        long userId = TestUserEntityGenerator.randomId();

        totalPageInfoCache.removeUser(userId);

        verify(cache, times(1)).evict(getKey(userId, CountInfo.Open));
        verify(cache, times(1)).evict(getKey(userId, CountInfo.Close));
        verify(cache, times(1)).evict(getKey(userId, CountInfo.Deleted));
    }

    @Test
    void remove_ShouldRemovedUserOpenInfo_WhenOpen() {
        long userId = TestUserEntityGenerator.randomId();

        totalPageInfoCache.remove(userId, CountInfo.Open);

        verify(cache, times(1)).evict(getKey(userId, CountInfo.Open));
    }

    @Test
    void remove_ShouldRemovedUserCloseInfo_WhenClose() {
        long userId = TestUserEntityGenerator.randomId();

        totalPageInfoCache.remove(userId, CountInfo.Close);

        verify(cache, times(1)).evict(getKey(userId, CountInfo.Close));
    }

    @Test
    void remove_ShouldRemovedUserDeletedInfo_WhenDeleted() {
        long userId = TestUserEntityGenerator.randomId();

        totalPageInfoCache.remove(userId, CountInfo.Deleted);

        verify(cache, times(1)).evict(getKey(userId, CountInfo.Deleted));
    }

    private String getKey(long userId, CountInfo countInfo){
        return new StringBuilder()
                .append(userId)
                .append("_")
                .append(countInfo.name())
                .toString();
    }
}