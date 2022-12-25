package com.melody.melody.adapter.web.common;

import lombok.Value;

import java.util.List;

@Value
public class PageResponse<T> {
    private final List<T> list;

    private final int count;

    private final long totalCount;

    private final int totalPage;

}
