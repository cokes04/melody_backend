package com.melody.melody.adapter.cache.post;

import com.melody.melody.adapter.cache.CacheType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserPostTotalCacheConcurrencyTest {
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
    void update_ShouldThreadSafe() throws Exception {
        final long userId = 100;

        Runnable runnable = () -> {
            totalPageInfoCache.update(userId, CountInfo.Open, CountInfo.Close);
            totalPageInfoCache.update(userId, CountInfo.Open, CountInfo.Deleted);
            try { Thread.sleep(1); } catch (InterruptedException e) { }

            totalPageInfoCache.update(userId, CountInfo.Close, CountInfo.Open);
            totalPageInfoCache.update(userId, CountInfo.Close, CountInfo.Deleted);
            try { Thread.sleep(1); } catch (InterruptedException e) { }

            totalPageInfoCache.update(userId, CountInfo.Deleted, CountInfo.Open);
            totalPageInfoCache.update(userId, CountInfo.Deleted, CountInfo.Close);
            try { Thread.sleep(1); } catch (InterruptedException e) { }


            totalPageInfoCache.update(userId, CountInfo.Open, CountInfo.Close);
            totalPageInfoCache.update(userId, CountInfo.Close, CountInfo.Deleted);
            totalPageInfoCache.update(userId, CountInfo.Close, CountInfo.Deleted);
            try { Thread.sleep(1); } catch (InterruptedException e) { }
        };

        String openKey = getKey(userId, CountInfo.Open);
        AtomicLong openValue = new AtomicLong(5000);

        String closeKey = getKey(userId, CountInfo.Close);
        AtomicLong closeValue = new AtomicLong(3000);

        String deletedKey = getKey(userId, CountInfo.Deleted);
        AtomicLong deletedValue = new AtomicLong(2000);

        when(cache.get(openKey, AtomicLong.class))
                .thenReturn(openValue);

        when(cache.get(closeKey, AtomicLong.class))
                .thenReturn(closeValue);

        when(cache.get(deletedKey, AtomicLong.class))
                .thenReturn(deletedValue);

        for (int i = 0; i < 1000; i++) {
            new Thread(runnable).start();
        }

        Thread.sleep(1000);

        Long openActual = totalPageInfoCache.get(userId, CountInfo.Open);
        Long closeActual = totalPageInfoCache.get(userId, CountInfo.Close);
        Long deletedActual = totalPageInfoCache.get(userId, CountInfo.Deleted);
        assertEquals(4000, openActual);
        assertEquals(2000, closeActual);
        assertEquals(4000, deletedActual);
    }

    @Test
    void increaseAndDecrease_ShouldThreadSafe() throws Exception {
        final long userId = 100;
        final CountInfo countInfo = CountInfo.Open;

        Runnable runnable = () -> {
            totalPageInfoCache.increase(userId, countInfo);
            totalPageInfoCache.decrease(userId, countInfo);
            try { Thread.sleep(1); } catch (InterruptedException e) { }

            totalPageInfoCache.increase(userId, countInfo);
            totalPageInfoCache.decrease(userId, countInfo);
            totalPageInfoCache.increase(userId, countInfo);
            totalPageInfoCache.increase(userId, countInfo);
            try { Thread.sleep(1); } catch (InterruptedException e) { }
        };

        String key = getKey(userId, countInfo);
        AtomicLong value = new AtomicLong(0);

        when(cache.get(key, AtomicLong.class))
                .thenReturn(value);

        for (int i = 0; i < 1000; i++) {
            new Thread(runnable).start();
        }

        Thread.sleep(1000);

        Long actual = totalPageInfoCache.get(userId, CountInfo.Open);
        assertEquals(2000, actual);
    }


    private String getKey(long userId, CountInfo countInfo){
        return new StringBuilder()
                .append(userId)
                .append("_")
                .append(countInfo.name())
                .toString();
    }
}
