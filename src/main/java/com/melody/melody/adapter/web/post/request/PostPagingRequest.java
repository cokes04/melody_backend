package com.melody.melody.adapter.web.post.request;

import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostSort;
import lombok.*;

import javax.validation.constraints.*;

@Setter(value = AccessLevel.PROTECTED)
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class PostPagingRequest {

    @PositiveOrZero
    private int page;

    @Min(value = 10)
    private int size;

    private PostSort sorting;

    public PagingInfo<PostSort> toCommand(){
        return new PagingInfo<PostSort>(page, size, sorting);
    }
}
