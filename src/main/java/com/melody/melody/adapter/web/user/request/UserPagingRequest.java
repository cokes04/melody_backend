package com.melody.melody.adapter.web.user.request;

import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.UserSort;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Setter(value = AccessLevel.PROTECTED)
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UserPagingRequest {

    @PositiveOrZero
    private int page;

    @Min(value = 10)
    private int size;

    public PagingInfo<UserSort> toPagingInfo(){
        return new PagingInfo<UserSort>(page, size, UserSort.recent_activity);
    }
}
