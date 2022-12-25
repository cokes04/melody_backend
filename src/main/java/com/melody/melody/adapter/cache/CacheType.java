package com.melody.melody.adapter.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public enum CacheType {
    UserDetails(
            Provider.Caffeine,
            "userDetails",
            3 * 60,
            null,
            1000
    );

    private final Provider provider;
    private final String cacheName;
    private final Integer expireAfterAccess; // SECONDS
    private final Integer expireAfterWrite; // SECONDS
    private final int maximumSize;


    public enum Provider{
      Caffeine;
    }

}