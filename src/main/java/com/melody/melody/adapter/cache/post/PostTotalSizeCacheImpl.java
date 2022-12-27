package com.melody.melody.adapter.cache.post;

import com.melody.melody.adapter.cache.CacheAdapter;
import com.melody.melody.adapter.persistence.postdetail.PostTotalSizeCache;
import com.melody.melody.application.dto.Open;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@CacheAdapter
@RequiredArgsConstructor
public class PostTotalSizeCacheImpl implements PostTotalSizeCache {
    private final UserPostTotalCache postTotalCache;

    @Override
    public Optional<Long> getTotalSize(User.UserId userId, Open open) {
        long userIdValue = userId.getValue();

        Long openCount, closeCount;
        switch (open){
            case Everything:
                openCount = postTotalCache.get(userIdValue, CountInfo.Open);
                if (Objects.isNull(openCount)) return Optional.empty();
                closeCount = postTotalCache.get(userIdValue, CountInfo.Close);
                if (Objects.isNull(closeCount)) return Optional.empty();
                return Optional.of(openCount + closeCount);

            case OnlyOpen:
                openCount = postTotalCache.get(userIdValue, CountInfo.Open);
                return Optional.ofNullable(openCount);

            case OnlyClose:
                closeCount = postTotalCache.get(userIdValue, CountInfo.Close);
                return Optional.ofNullable(closeCount);
        }

        return Optional.empty();
    }

    @Override
    public void putTotalSize(User.UserId userId, Open open, long totalSize) {
        long userIdValue = userId.getValue();

        switch (open){
            case OnlyOpen:
                postTotalCache.put(userIdValue, CountInfo.Open, totalSize);

            case OnlyClose:
                postTotalCache.put(userIdValue, CountInfo.Close, totalSize);
        }
    }

    @Override
    public void putDeletedTotalSize(User.UserId userId, long totalSize) {
        postTotalCache.put(userId.getValue(), CountInfo.Deleted, totalSize);
    }
}