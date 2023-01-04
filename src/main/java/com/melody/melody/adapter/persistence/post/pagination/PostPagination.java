package com.melody.melody.adapter.persistence.post.pagination;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PostPagination {
    private final Long startPostId;
    private final boolean startInclude;
    private final long offset;
}
