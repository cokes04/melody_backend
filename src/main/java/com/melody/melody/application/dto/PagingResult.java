package com.melody.melody.application.dto;

import lombok.Value;

import java.util.List;

@Value
public class PagingResult<T> {
    private final List<T> list;
    private final int count;
    private final long totalCount;
    private final int totalPage;
}
