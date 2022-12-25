package com.melody.melody.adapter.cache;

import com.melody.melody.domain.event.UserWithdrew;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CacheRemover {
    private final CacheManager cacheManager;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Async
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = UserWithdrew.class)
    public void handle(UserWithdrew event) {
        Cache cache = cacheManager.getCache(CacheType.UserDetails.getCacheName());

        if (Objects.nonNull(cache)){
            cache.evict(new User.UserId(event.getUserId()));

        } else{
            logger.error("{} : Not Found Cache", CacheType.UserDetails.getCacheName());

        }
    }
}
