package com.melody.melody.adapter.persistence.post.pagination;

import com.melody.melody.adapter.persistence.post.size.SizeInfo;
import com.melody.melody.config.CacheType;
import com.melody.melody.domain.model.Identity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class UserPostPaginationCache {
    public static final int initialUnit = 20;
    public static final int initialIndexCount = 20;
    public static final int maxIndex = 2539;

    private final CacheManager cacheManager;

    // key : userId_sizeInfo_(asc||desc)_index  // 333O(pen)A(sc)4 -> 333번 유저의 id 오름차순 100((4 + 1) * 20)번째 공개 게시물
    // value : postId

    public Result get(Identity userId, SizeInfo sizeInfo, boolean asc, int offset) {
        long userIdValue = userId.getValue();
        String key;
        Long value;

        Integer initialIndex = getIndexFromPostNum(offset);
        if (Objects.isNull(initialIndex))
            return Result.builder()
                    .postPagination(getOnlyOffsetPostPagination(offset, asc))
                    .neededIndexs(0)
                    .countPerIndex(initialUnit)
                    .build();

        initialIndex = Math.min(initialIndex, maxIndex);
        int index = initialIndex;

        int postNum;
        while (index >= 0){
            key = getKey(userIdValue, sizeInfo, asc, index);
            value = getValue(key);

            if (value != null){
                postNum = getPostNumFromIndex(index);

                return Result.builder()
                        .postPagination(
                                PostPagination.builder()
                                        .startPostId(value)
                                        .startInclude(false)
                                        .offset(offset - postNum)
                                        .build()
                        )
                        .neededIndexs(initialIndex - index)
                        .countPerIndex(getUnit(getPartFromIndex(initialIndex)))
                        .build();
            }

            index -= 1;
        }

        return Result.builder()
                .postPagination(getOnlyOffsetPostPagination(offset, asc))
                .neededIndexs(initialIndex)
                .countPerIndex(getUnit(getPartFromIndex(initialIndex)))
                .build();
    }

    public void put(Identity userId, SizeInfo sizeInfo, boolean asc, long offset, List<Long> idList){
        long userIdValue = userId.getValue();

        String key;
        int unit, keyIndex, lastKeyIndex, postCount, part = 0;
        int idIndex = -1;

        escape : while (true) {
            unit = getUnit(part);
            keyIndex = getStartIndex(part);

            if (offset > 0) {
                postCount = getPostCount(part);

                if (postCount <= offset) {
                    offset -= postCount;
                    part += 1;
                    continue;
                }

                keyIndex += offset / unit;
                idIndex -= offset % unit;
                offset = 0;
            }

            lastKeyIndex = getLastIndex(part);
            for (; keyIndex <= lastKeyIndex; keyIndex++) {
                idIndex += unit;
                if (idIndex >= idList.size() || keyIndex > maxIndex)
                    break escape;

                key = getKey(userIdValue, sizeInfo, asc, keyIndex);
                putValue(key, idList.get(idIndex));
            }

            part += 1;
        }
    }

    public void evict(Identity userId){
        evict(userId, SizeInfo.Open);
        evict(userId, SizeInfo.Close);
    }


    public void evict(Identity userId, boolean asc){
        evict(userId, SizeInfo.Open, asc);
        evict(userId, SizeInfo.Close, asc);
    }


    public void evict(Identity userId, Identity postId){
        evict(userId, SizeInfo.Open, true, postId);
        evict(userId, SizeInfo.Open, false, postId);
        evict(userId, SizeInfo.Close, true, postId);
        evict(userId, SizeInfo.Close, false, postId);
        }

    public void evict(Identity userId, SizeInfo sizeInfo, boolean asc){
        Cache cache = getPageInfoCache();
        String key;

        for (int i = 0; i <= maxIndex; i++){
            key = getKey(userId.getValue(), sizeInfo, asc, i);
            cache.evict(key);
        }
    }

    private void evict(Identity userId, SizeInfo sizeInfo){
        evict(userId, sizeInfo, true);
        evict(userId, sizeInfo, false);
    }

    public void evict(Identity userId, SizeInfo sizeInfo, boolean asc, Identity postId){
        Cache cache = getPageInfoCache();
        com.github.benmanes.caffeine.cache.Cache<String, Long> caffeinCache =
                (com.github.benmanes.caffeine.cache.Cache<String, Long>) getPageInfoCache().getNativeCache();

        List<String> keys = IntStream.range(0, maxIndex+1)
                .mapToObj(i -> getKey(userId.getValue(), sizeInfo, asc, i))
                .collect(Collectors.toList());

        Map<String, Long> map = caffeinCache.getAllPresent(keys);

        Long postIdValue = postId.getValue(), value;
        for (Map.Entry<String, Long> entry : map.entrySet()){
            value = entry.getValue();

            if ((asc && value >= postIdValue)
                    || (!asc && value <= postIdValue)) {
                cache.evict(entry.getKey());
            }
        }
    }


    private String getKey(long userId, SizeInfo sizeInfo, boolean asc, int index){
        return new StringBuilder()
                .append(userId)
                .append(sizeInfo.getSymbol())
                .append(asc ? "A" : "D")
                .append(index)
                .toString();
    }

    @Nullable
    private Long getValue(String key){
        return getPageInfoCache().get(key, Long.class);
    }

    private void putValue(String key, Long value){
        getPageInfoCache().put(key, value);
    }

    private Cache getPageInfoCache(){
        return cacheManager.getCache(CacheType.UserPostPage.getCacheName());
    }

    private int getUnit(int part){
        return initialUnit * (int) Math.pow(2, part);
    }

    private int getIndexCount(int part){
        return initialIndexCount * (int) Math.pow(2, part);
    }

    private int getStartIndex(int part){
        return part == 0 ? 0 : getLastIndex(part-1) + 1;
    }

    private int getLastIndex(int part){
        return getStartIndex(part) + getUnit(part) - 1;
    }

    private int getPostCount(int part){
        return getUnit(part) * getIndexCount(part);
    }

    private int getStartPost(int part){
        return part == 0 ? 0 : getLastPost(part-1) + 1;
    }

    private int getLastPost(int part){
        return getStartPost(part) + getPostCount(part) - 1;
    }

    @Nullable
    private Integer getIndexFromPostNum(int postNum){
        if (postNum < initialUnit) return null;

        int part = 0, indexCount = 0, postCount;
        while (true){
            postCount = getPostCount(part);

            if (postNum > postCount) {
                indexCount += getIndexCount(part);
                postNum -= postCount;
                part += 1;
                continue;
            }

            return (indexCount - 1) + (postNum / getUnit(part));
        }
    }

    private int getPostNumFromIndex(int index){
        int part = 0, postCount = 0, indexCount;

        while (true){
            indexCount = getIndexCount(part);

            if (index >= indexCount) {
                postCount += getPostCount(part);
                index -= indexCount;
                part += 1;
                continue;
            }

            return postCount  + ((index + 1) * getUnit(part));
        }
    }

    private int getPartFromIndex(int index){
        int part = 0;

        while (index > getLastIndex(part))
            part += 1;

        return part;
    }

    private PostPagination getOnlyOffsetPostPagination(int offset, boolean asc){
        return PostPagination.builder()
                .startPostId(asc ? 0L : Long.MAX_VALUE)
                .startInclude(true)
                .offset(offset)
                .build();
    }

    @Value
    @Builder
    public static class Result{
        PostPagination postPagination;
        int neededIndexs;
        int countPerIndex;
    }

    /*
        * part 0
          unit = 20
          index count = 20
          index range = 0 ~ 19
          post count = 400
          post range = 0 ~ 399

        * part 1
          unit = 40
          index count = 40
          index range = 20 ~ 59
          post count = 1600
          post range = 400 ~ 1999

        * part 2
          unit = 80
          index count = 80
          index range = 60 ~ 139
          post count = 6400
          post range = 2000 ~ 8399

        * part 3
          unit = 160
          index count = 160
          index range = 140 ~ 299
          post count = 25600
          post range = 8400 ~ 33999

        // part 4 = post 102,400개 / 총 136,400 개 / index 320 / last index 619
        // part 5 = post 409,600개 / 총 546,000 개 / index 640 / last index 1259
        // part 6 = post 1,638,400개 / 총 2,184,400 개 / index 1280 / last index 2539

    */
}
