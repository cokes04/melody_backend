package com.melody.melody.adapter.cache.post;

import com.melody.melody.adapter.cache.CacheType;
import lombok.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserPostTotalCache {
    private final CacheManager cacheManager;

    @Nullable
    public Long get(long userId, CountInfo info) {
        String key = getKey(userId, info);
        return getValue(key);
    }

    public void put(long userId, CountInfo info, long value){
        String key = getKey(userId, info);
        putValue(key, value);
    }

    public void increase(long userId, CountInfo countInfo){
        update(userId, null, countInfo);
    }

    public void decrease(long userId, CountInfo countInfo){
        update(userId, countInfo, null);
    }

    public void update(long userId, @Nullable CountInfo previous, @Nullable CountInfo after){
        if (Objects.nonNull(previous)){
            String previousKey = getKey(userId, previous);
            Long previousValue = getValue(previousKey);

            if (Objects.nonNull(previousValue)){
                putValue(previousKey, previousValue - 1);
            }
        }

        if (Objects.nonNull(after)){
            String afterKey = getKey(userId, after);
            Long afterValue = getValue(afterKey);

            if (Objects.nonNull(afterValue)){
                putValue(afterKey, afterValue + 1);
            }
        }
    }

    public void removeUser(long userId){
        remove(userId, CountInfo.Open);
        remove(userId, CountInfo.Close);
        remove(userId, CountInfo.Deleted);
    }

    public void remove(long userId, CountInfo countInfo){
        String key = getKey(userId, countInfo);
        evict(key);
    }

    private String getKey(long userId, CountInfo countInfo){
        return new StringBuilder()
                .append(userId)
                .append("_")
                .append(countInfo.name())
                .toString();
    }

    private Long getValue(String key){
        return getCache().get(key, Long.class);
    }

    private void putValue(String key, long value){
        getCache().put(key, value);
    }

    private void evict(String key){
        getCache().evict(key);
    }


    private Cache getCache(){
        return cacheManager.getCache(CacheType.UserPostTotal.getCacheName());
    }

}
