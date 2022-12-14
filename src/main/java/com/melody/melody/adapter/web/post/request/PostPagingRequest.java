package com.melody.melody.adapter.web.post.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.melody.melody.adapter.web.converter.StringToPostSortConverter;
import com.melody.melody.adapter.web.converter.YNToBooleanConverter;
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

    @NotNull
    @JsonDeserialize(converter = StringToPostSortConverter.class)
    private PostSort sorting;

    public PagingInfo<PostSort> toPagingInfo(){
        return new PagingInfo<PostSort>(page, size, sorting);
    }
}
