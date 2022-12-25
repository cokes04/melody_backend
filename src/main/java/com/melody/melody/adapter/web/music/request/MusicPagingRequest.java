package com.melody.melody.adapter.web.music.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.melody.melody.adapter.web.converter.StringToPostSortConverter;
import com.melody.melody.application.dto.MusicSort;
import com.melody.melody.application.dto.PagingInfo;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Setter(value = AccessLevel.PROTECTED)
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class MusicPagingRequest {

    @PositiveOrZero
    private int page;

    @Min(value = 10)
    private int size;

    @NotNull
    @JsonDeserialize(converter = StringToPostSortConverter.class)
    private MusicSort sorting;

    public PagingInfo<MusicSort> toPagingInfo(){
        return new PagingInfo<MusicSort>(page, size, sorting);
    }
}
