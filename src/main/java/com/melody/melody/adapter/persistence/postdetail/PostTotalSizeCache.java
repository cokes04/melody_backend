package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.User;
import lombok.Value;

import java.util.Optional;

public interface PostTotalSizeCache {
    Optional<Long> getTotalSize(Identity userId, Open open);
    void putTotalSize(Identity userId, Open open, long totalSize);
    void putDeletedTotalSize(Identity userId, long totalSize);
}