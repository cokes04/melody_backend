package com.melody.melody.adapter.persistence.post.postPagination;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PostPaginationInfo {
    private final Long startPostId;
    private final boolean startInclude;
    private final long offset;
}
