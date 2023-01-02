package com.melody.melody.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.cache.CacheManager;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    @Primary
    public CacheManager compositeCacheManager() {
        CompositeCacheManager cacheManager = new CompositeCacheManager();

        List<org.springframework.cache.CacheManager> managers = new ArrayList<>();
        managers.add(simpleCacheManager());

        cacheManager.setCacheManagers(managers);

        return cacheManager;
    }

    @Bean
    public SimpleCacheManager simpleCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(getCaffeineCacheList());

        return cacheManager;
    }

    private List<CaffeineCache> getCaffeineCacheList(){
        return Arrays.stream(CacheType.values())
                .filter(c -> c.getProvider().equals(CacheType.Provider.Caffeine))
                .map(this::getCaffeineCache)
                .collect(Collectors.toList());
    }

    private CaffeineCache getCaffeineCache(CacheType cacheType){
        Caffeine caffeine = Caffeine.newBuilder().recordStats();
        caffeine.maximumSize(cacheType.getMaximumSize());

        if (Objects.nonNull(cacheType.getExpireAfterAccess()))
            caffeine.expireAfterAccess(cacheType.getExpireAfterAccess(), TimeUnit.SECONDS);

        if (Objects.nonNull(cacheType.getExpireAfterWrite()))
            caffeine.expireAfterWrite(cacheType.getExpireAfterWrite(), TimeUnit.SECONDS);

        return new CaffeineCache(cacheType.getCacheName(), caffeine.build());
    }
}
