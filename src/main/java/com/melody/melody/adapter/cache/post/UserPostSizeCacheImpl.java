package com.melody.melody.adapter.cache.post;

import com.melody.melody.adapter.cache.CacheAdapter;
import com.melody.melody.adapter.cache.CacheType;
import com.melody.melody.adapter.persistence.post.SizeInfo;
import com.melody.melody.domain.model.Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;


@CacheAdapter
@RequiredArgsConstructor
public class UserPostSizeCacheImpl {
    private final CacheManager cacheManager;

    @Nullable
    public Long get(Identity userId, SizeInfo sizeInfo){
        String key = getKey(userId, sizeInfo);
        return getValue(key);
    }

    private String getKey(Identity userId, SizeInfo sizeInfo){
        return new StringBuilder()
                .append(userId.getValue())
                .append("_")
                .append(sizeInfo.name())
                .toString();
    }

    @Nullable
    private Long getValue(String key){
        return getCache().get(key, Long.class);
    }

    private void putValue(String key, Long value){
        getCache().put(key, value);
    }

    private void evict(String key){
        getCache().evict(key);
    }

    private Cache getCache(){
        return cacheManager.getCache(CacheType.UserPostSize.getCacheName());
    }
}