package com.melody.melody.adapter.persistence.post.pagination;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PostPagination {
    private final Long startPostId;
    private final boolean startInclude;
    private final long offset;
    private final List<Long> inIdList;
    private final boolean noResult;

    public boolean emptyInIdList(){
        return inIdList == null || inIdList.size() == 0;
    }
}
