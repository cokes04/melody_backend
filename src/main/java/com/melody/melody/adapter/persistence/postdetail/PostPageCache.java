package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.User;
import lombok.Value;

import java.util.Optional;

public interface PostPageCache {
    Optional<Result> getByUserId(User.UserId userId, Open open, PagingInfo<PostSort> postPaging);

    @Value
    class Result {
        private final long firstPostId;
        private final long offset;
    }

}