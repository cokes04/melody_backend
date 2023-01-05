package com.melody.melody.adapter.persistence.post.pagination;

import com.melody.melody.adapter.persistence.post.size.SizeInfo;
import com.melody.melody.config.CacheType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.transaction.NotSupportedException;
import java.util.*;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class UserPostPaginationCacheTest {
    private static int maxIndex = UserPostPaginationCache.maxIndex;
    private static int indexCount = maxIndex + 1;
    private static int maxPostCount = 2184400;
    private static int lastPartPostCountPerIndex = 1280;

    private UserPostPaginationCache paginationInfoCache;
    private CacheManager cacheManager;
    private Cache cache;
    private com.github.benmanes.caffeine.cache.Cache<String, Long> caffeinCache;


    @BeforeEach
    void setUp() {
        caffeinCache = Mockito.mock(com.github.benmanes.caffeine.cache.Cache.class);
        cache = Mockito.mock(Cache.class);
        cacheManager = Mockito.mock(CacheManager.class);
        paginationInfoCache = new UserPostPaginationCache(cacheManager);

        when(cacheManager.getCache(CacheType.UserPostPage.getCacheName()))
                .thenReturn(cache);
    }




    @Test
    void get_ShouldReturnOffset4_When444Offset_And_HaveIndex20() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 20), Long.class))
                .thenReturn(888L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 444);

        assertEquals(4, result.getPostPagination().getOffset());
        assertEquals(888L, result.getPostPagination().getStartPostId());
        assertEquals(0, result.getNeededIndexs());
        assertEquals(40, result.getCountPerIndex());
    }

    @Test
    void get_ShouldReturnOffset64_When444Offset_And_HaveIndex18() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 18), Long.class))
                .thenReturn(888L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 444);

        assertEquals(64, result.getPostPagination().getOffset());
        assertEquals(888L, result.getPostPagination().getStartPostId());
        assertEquals(2, result.getNeededIndexs());
        assertEquals(40, result.getCountPerIndex());
    }

    @Test
    void get_ShouldReturn444Offset_When444Offset_ASC_And_NotHaveIndex() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 444);

        assertEquals(444, result.getPostPagination().getOffset());
        assertEquals(0, result.getPostPagination().getStartPostId());
        assertEquals(20, result.getNeededIndexs());
        assertEquals(40, result.getCountPerIndex());
    }

    @Test
    void get_ShouldReturn444Offset_When444Offset_DESC_And_NotHaveIndex() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, false, 444);

        assertEquals(444, result.getPostPagination().getOffset());
        assertEquals(Long.MAX_VALUE, result.getPostPagination().getStartPostId());
        assertEquals(20, result.getNeededIndexs());
        assertEquals(40, result.getCountPerIndex());
    }

    @Test
    void get_ShouldReturnOffset5_When25Offset_And_HaveIndex0() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 0), Long.class))
                .thenReturn(888L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 25);

        assertEquals(5, result.getPostPagination().getOffset());
        assertEquals(888L, result.getPostPagination().getStartPostId());
        assertEquals(0, result.getNeededIndexs());
        assertEquals(20, result.getCountPerIndex());
    }

    @Test
    void get_ShouldReturnOffset0_When20Offset_And_HaveIndex0() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 0), Long.class))
                .thenReturn(888L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 20);

        assertEquals(0, result.getPostPagination().getOffset());
        assertEquals(888L, result.getPostPagination().getStartPostId());
        assertEquals(0, result.getNeededIndexs());
        assertEquals(20, result.getCountPerIndex());
    }

    @Test
    void get_ShouldReturn15Offest_When15Offset_And_ASC() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 0), Long.class))
                .thenReturn(5L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, 15);
        assertEquals(15, result.getPostPagination().getOffset());
        assertEquals(0, result.getPostPagination().getStartPostId());
        assertEquals(0, result.getNeededIndexs());
        assertEquals(20, result.getCountPerIndex());
    }

    @Test
    void get_ShouldReturn15Offest_When15Offset_And_DESC() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, 0), Long.class))
                .thenReturn(5L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, false, 15);
        assertEquals(15, result.getPostPagination().getOffset());
        assertEquals(Long.MAX_VALUE, result.getPostPagination().getStartPostId());
        assertEquals(0, result.getNeededIndexs());
        assertEquals(20, result.getCountPerIndex());
    }

    @Test
    void get_ShouldReturnMaxIndexOffest_WhenOffsetBeyondMaxIndex() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        when(cache.get(getKey(userId.getValue(), sizeInfo, true, maxIndex), Long.class))
                .thenReturn(5L);

        UserPostPaginationCache.Result result = paginationInfoCache.get(userId, sizeInfo, true, maxPostCount + 1000);
        assertEquals(1000, result.getPostPagination().getOffset());
        assertEquals(5L, result.getPostPagination().getStartPostId());
        assertEquals(0, result.getNeededIndexs());
        assertEquals(lastPartPostCountPerIndex, result.getCountPerIndex());

        verify(cache, times(1))
                .get(anyString(), eq(Long.class));
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



    @Test
    void put_ShouldPutNotDataInCache_WhenGreaterThanMaxPostCountOffset() {
        List<Long> idList = getIdList(1000, true);
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        paginationInfoCache.put(userId, sizeInfo, true, maxPostCount + 1, idList);

        verify(cache, times(0))
                .put(anyString(), anyLong());
    }


    @Test
    void put_ShouldPutNotDataInCache_WhenLikeMaxPostCountOffset() {
        List<Long> idList = getIdList(1000, true);
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        paginationInfoCache.put(userId, sizeInfo, true, maxPostCount, idList);

        verify(cache, times(0))
                .put(anyString(), anyLong());
    }

    @Test
    void put_ShouldPutPartDataInCache_WhenLessThanMaxPostCountOffset() {
        List<Long> idList = getIdList(1000, true);
        Identity userId = TestUserDomainGenerator.randomUserId();
        SizeInfo sizeInfo = SizeInfo.Open;

        paginationInfoCache.put(userId, sizeInfo, true, maxPostCount - 1, idList);

        verify(cache, times(1))
                .put(anyString(), anyLong());
    }

    @Test
    void evict_userId_ShouldEvictEveryUserPagination() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        paginationInfoCache.evict(userId);

        List<String> keyRegexList = List.of(
                getKey(userId.getValue(), SizeInfo.Open, true, null) + "\\d+",
                getKey(userId.getValue(), SizeInfo.Open, false, null) + "\\d+",
                getKey(userId.getValue(), SizeInfo.Close, true, null) + "\\d+",
                getKey(userId.getValue(), SizeInfo.Close, false, null) + "\\d+"
        );

        keyRegexList.stream()
                .forEach(
                        r -> verify(cache, times(indexCount)).evict(matches(r))
                );

        verify(cache, times(indexCount * 4)).evict(anyString());
    }

    @Test
    void evict_userId_asc_ShouldEvictUserASCPagination_WhenASC() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        paginationInfoCache.evict(userId, true);

        List<String> keyRegexList = List.of(
                getKey(userId.getValue(), SizeInfo.Open, true, null) + "\\d+",
                getKey(userId.getValue(), SizeInfo.Close, true, null) + "\\d+"
        );

        keyRegexList.stream()
                .forEach(
                        r -> verify(cache, times(indexCount)).evict(matches(r))
                );

        verify(cache, times(indexCount * 2)).evict(anyString());
    }

    @Test
    void evict_userId_asc_ShouldEvictUserDESCPagination_WhenDESC() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        paginationInfoCache.evict(userId, false);

        List<String> keyRegexList = List.of(
                getKey(userId.getValue(), SizeInfo.Open, false, null) + "\\d+",
                getKey(userId.getValue(), SizeInfo.Close, false, null) + "\\d+"
                );

        keyRegexList.stream()
                .forEach(
                        r -> verify(cache, times(indexCount)).evict(matches(r))
                );

        verify(cache, times(indexCount * 2)).evict(anyString());
    }

    @Test
    void evict_userId_postId_ShouldEvictAfterPostIdUserPagination() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        Identity postId = Identity.from(59);

        when(cache.getNativeCache())
                .thenReturn(caffeinCache);

        when(caffeinCache.getAllPresent(any()))
                .thenAnswer( a -> {
                    List<String> arg = a.getArgument(0, List.class);
                    List<Long> idList = LongStream.range(0, indexCount).collect(
                            ArrayList::new,
                            ArrayList::add,
                            (zz, xx) -> new NotSupportedException()
                    );

                    Map<String, Long> result = new HashMap<>();
                    for (int i = 0; i < arg.size(); i++)
                        result.put(arg.get(i), idList.get(i));

                    return result;
                });

        paginationInfoCache.evict(userId, postId);

        verify(cache, times(indexCount - 60 + 1)).evict(matches(getKey(userId.getValue(), SizeInfo.Open, true, null) + "\\d+"));
        verify(cache, times(indexCount - 60 + 1)).evict(matches(getKey(userId.getValue(), SizeInfo.Close, true, null) + "\\d+"));
        verify(cache, times(60)).evict(matches(getKey(userId.getValue(), SizeInfo.Open, false, null) + "\\d+"));
        verify(cache, times(60)).evict(matches(getKey(userId.getValue(), SizeInfo.Close, false, null) + "\\d+"));

        verify(cache, times(5082)).evict(anyString());
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

    private String getKey(long userId, SizeInfo sizeInfo, boolean asc, Integer index){
        return new StringBuilder()
                .append(userId)
                .append(sizeInfo.getSymbol())
                .append(asc ? "A" : "D")
                .append(Objects.nonNull(index) ? index : "")
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