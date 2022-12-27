package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.model.User;
import lombok.Value;

import java.util.Optional;

public interface PostTotalSizeCache {
    Optional<Long> getTotalSize(User.UserId userId, Open open);
    void putTotalSize(User.UserId userId, Open open, long totalSize);
    void putDeletedTotalSize(User.UserId userId, long totalSize);
}