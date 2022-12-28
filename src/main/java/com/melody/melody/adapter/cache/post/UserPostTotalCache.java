package com.melody.melody.adapter.cache.post;

import com.melody.melody.adapter.cache.CacheType;
import lombok.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class UserPostTotalCache {
    private final CacheManager cacheManager;

    @Nullable
    public Long get(long userId, CountInfo info) {
        String key = getKey(userId, info);
        AtomicLong value = getValue(key);
        return Objects.isNull(value) ? null : value.get();
    }

    public void put(long userId, CountInfo info, long value){
        String key = getKey(userId, info);
        AtomicLong existingValue = getValue(key);

        if (Objects.isNull(existingValue))
            putValue(key, new AtomicLong(value));
        else
            existingValue.set(value);
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
            AtomicLong previousValue = getValue(previousKey);

            if (Objects.nonNull(previousValue))
                previousValue.decrementAndGet();
        }

        if (Objects.nonNull(after)){
            String afterKey = getKey(userId, after);
            AtomicLong afterValue = getValue(afterKey);

            if (Objects.nonNull(afterValue))
                afterValue.incrementAndGet();
        }
    }

    public void removeUser(long userId){
        for (CountInfo countInfo : CountInfo.values())
            remove(userId, countInfo);
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

    @Nullable
    private AtomicLong getValue(String key){
        // 원본 객체 반환
        return getCache().get(key, AtomicLong.class);
    }

    private void putValue(String key, AtomicLong value){
        getCache().put(key, value);
    }

    private void evict(String key){
        getCache().evict(key);
    }


    private Cache getCache(){
        return cacheManager.getCache(CacheType.UserPostTotal.getCacheName());
    }
}
