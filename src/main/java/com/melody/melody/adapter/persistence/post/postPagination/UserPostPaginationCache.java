package com.melody.melody.adapter.persistence.post.postPagination;

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
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserPostPaginationCache {
    private final CacheManager cacheManager;

    // key : userId_sizeInfo_(asc||desc)_index  // 333O(pen)A(sc)4 -> 333번 유저의 id 오름차순 100((4 + 1) * 20)번째 공개 게시물
    // value : postId

    private final int initialUnit = 20;
    private final int initialIndexCount = 20;

    public Result get(Identity userId, SizeInfo sizeInfo, boolean asc, int offset) {
        long userIdValue = userId.getValue();
        String key;
        Long value;

        Integer initialIndex = getIndexFromPostNum(offset);
        if (Objects.isNull(initialIndex)) {
            return Result.builder()
                    .postPaginationInfo(
                            PostPaginationInfo.builder()
                                    .startPostId(null)
                                    .startInclude(false)
                                    .offset(offset)
                                    .build()
                    )
                    .neededPages(0)
                    .build();
        }

        int index = initialIndex;
        while (index >= 0){
            key = getKey(userIdValue, sizeInfo, asc, index);
            value = getValue(key);

            if (value != null){
                return Result.builder()
                        .postPaginationInfo(
                                PostPaginationInfo.builder()
                                        .startPostId(value)
                                        .startInclude(false)
                                        .offset(offset - getPostNumFromIndex(index))
                                        .build()
                        )
                        .neededPages(initialIndex - index)
                        .build();
            }

            index -= 1;
        }

        return Result.builder()
                .postPaginationInfo(
                        PostPaginationInfo.builder()
                                .startPostId(null)
                                .startInclude(false)
                                .offset(offset)
                                .build()
                )
                .neededPages(initialIndex)
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
                if (idIndex >= idList.size())
                    break escape;

                key = getKey(userIdValue, sizeInfo, asc, keyIndex);
                putValue(key, idList.get(idIndex));
            }

            part += 1;
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
        int part = 0, postCount = 0, lastIndex;
        while (true){
            lastIndex = getLastIndex(part);
            if (index > lastIndex) {
                postCount += getPostCount(part);
                index -= (lastIndex + 1);
                part += 1;
                continue;
            }

            return postCount + (index + 1) * getUnit(part);
        }
    }

    @Value
    @Builder
    public static class Result{
        PostPaginationInfo postPaginationInfo;
        int neededPages;
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

        * part 4 ...
    */
}
