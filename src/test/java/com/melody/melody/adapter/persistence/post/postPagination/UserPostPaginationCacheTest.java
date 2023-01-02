package com.melody.melody.adapter.persistence.post.postPagination;

import com.melody.melody.adapter.persistence.post.size.SizeInfo;
import com.melody.melody.config.CacheType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPostPaginationCacheTest {
    private UserPostPaginationCache paginationInfoCache;
    private CacheManager cacheManager;
    private Cache cache;

    @BeforeEach
    void setUp() {
        cache = Mockito.mock(Cache.class);
        cacheManager = Mockito.mock(CacheManager.class);
        paginationInfoCache = new UserPostPaginationCache(cacheManager);

        when(cacheManager.getCache(CacheType.UserPostPage.getCacheName()))
                .thenReturn(cache);
    }

    @Test
    void get_ShouldGetOffset4_When444Offset_And_HaveIndex20() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 20), Long.class))
                .thenReturn(888L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 444);

        assertEquals(4, result.getPostPaginationInfo().getOffset());
        assertEquals(888L, result.getPostPaginationInfo().getStartPostId());
        assertEquals(0, result.getNeededPages());
    }

    @Test
    void get_ShouldGetOffset64_When444Offset_And_HaveIndex18() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 18), Long.class))
                .thenReturn(888L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 444);

        assertEquals(64, result.getPostPaginationInfo().getOffset());
        assertEquals(888L, result.getPostPaginationInfo().getStartPostId());
        assertEquals(2, result.getNeededPages());
    }

    @Test
    void get_ShouldNull_When444Offset_And_NotHaveIndex() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;


        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 444);

        assertEquals(444, result.getPostPaginationInfo().getOffset());
        assertNull(result.getPostPaginationInfo().getStartPostId());
        assertEquals(20, result.getNeededPages());

    }

    @Test
    void get_ShouldGetOffset5_When25Offset_And_HaveIndex0() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 0), Long.class))
                .thenReturn(888L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 25);

        assertEquals(5, result.getPostPaginationInfo().getOffset());
        assertEquals(888L, result.getPostPaginationInfo().getStartPostId());
        assertEquals(0, result.getNeededPages());
    }

    @Test
    void get_ShouldGetOffset0_When20Offset_And_HaveIndex0() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 0), Long.class))
                .thenReturn(888L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 20);

        assertEquals(0, result.getPostPaginationInfo().getOffset());
        assertEquals(888L, result.getPostPaginationInfo().getStartPostId());
        assertEquals(0, result.getNeededPages());
    }

    @Test
    void get_ShouldGet15Offest_When15Offset() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 0), Long.class))
                .thenReturn(5L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 15);
        assertEquals(15, result.getPostPaginationInfo().getOffset());
        assertNull(result.getPostPaginationInfo().getStartPostId());
        assertEquals(0, result.getNeededPages());
    }

    @Test
    void put_ShouldPutDataInCache_When0Offset() {
        List<Long> idList = getIdList(600, true);
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        paginationInfoCache.put(userId, sizeInfo, true, 0, idList);

        verifyPutPart0(userId, sizeInfo, 0, idList);
        verifyPutPart1(userId, sizeInfo, 0, idList);
    }

    @Test
    void put_ShouldPutDataInCache_When66Offset() {
        List<Long> idList = getIdList(600, true);
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        paginationInfoCache.put(userId, sizeInfo, true, 66, idList);

        verifyPutPart0(userId, sizeInfo, 66, idList);
        verifyPutPart1(userId, sizeInfo, 66, idList);
    }

    @Test
    void put_ShouldPutDataInCache_When400Offset() {
        List<Long> idList = getIdList(600, true);
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        paginationInfoCache.put(userId, sizeInfo, true, 400, idList);

        verifyPutPart0(userId, sizeInfo, 400, idList);
        verifyPutPart1(userId, sizeInfo, 400, idList);
    }


    private List<Long> getIdList(int count, boolean asc){
        List<Long> idList = LongStream.range(0, count)
                 .collect(
                         ArrayList::new,
                         ArrayList::add,
                         (list1, list2) -> new NoSuchMethodException("xxx")
                 );

        if (!asc)
            Collections.reverse(idList);

        return idList;
    }

    private String getKey(long userId, SizeInfo sizeInfo, boolean asc, int index){
        return new StringBuilder()
                .append(userId)
                .append(sizeInfo.getSymbol())
                .append(asc ? "A" : "D")
                .append(index)
                .toString();
    }

    private void verifyPutPart0(Identity userId, SizeInfo sizeInfo, int offset, List<Long> idList){
        int unit = 20;
        int idStartIndex = - (offset % unit) - 1;

        int count = Math.max((idList.size() / unit), 0);
        int a = (offset / unit);
        count = count + a > 20 ? 20 - a : count;

        verify(cache, times(count))
                .put(anyString(), longThat( i -> idStartIndex + unit <= i && i <= idStartIndex + unit * (20 - a)));

        int idIndex;
        for (int i = 0; i < 20 - a; i++){
            idIndex = ((i + 1) * unit) + idStartIndex;

            if (idIndex > idList.size())
                break;

            verify(cache, times(1))
                    .put(
                            getKey(userId.getValue(), sizeInfo, true, i + a),
                            idList.get(idIndex)
                    );
        }
    }


    private void verifyPutPart1(Identity userId, SizeInfo sizeInfo, int offset, List<Long> idList){
        int unit = 40;
        int idStartIndex =  400 - (offset % unit) - 1;

        int count = Math.max(((idList.size() - 400) / unit), 0);
        int a = (offset / unit);
        count = count + a > 40 ? 40 - a : count;

        verify(cache, times(count))
                .put(anyString(), longThat( i -> idStartIndex + unit <= i && i <= idStartIndex + unit * (40 - a)));

        int idIndex;
        for (int i = 20; i < 59 - (offset / unit); i++){
            idIndex = ((i - 20 + 1) * unit) + idStartIndex;

            if (idIndex > idList.size())
                break;

            verify(cache, times(1))
                    .put(
                            getKey(userId.getValue(), sizeInfo, true, i + (offset / unit)),
                            idList.get(idIndex)
                    );
        }
    }
}