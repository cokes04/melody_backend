package com.melody.melody.adapter.web.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MusicResponse {
    private final Long musicId;

    private final String emotion;

    private final String explanation;

    private final String imageUrl;

    private final String status;
}
