package com.melody.melody.application.dto;

import lombok.Value;

@Value
public class PagingInfo<S> {
    private int page;

    private int size;

    private S sorting;
}
