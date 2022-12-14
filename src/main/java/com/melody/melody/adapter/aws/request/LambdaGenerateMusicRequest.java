package com.melody.melody.adapter.aws.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LambdaGenerateMusicRequest {
    @JsonProperty("music_id")
    private final long musicId;

    @JsonProperty("emotion")
    private final String emotion;

    @JsonProperty("music_len")
    private final int musicLength;

    @JsonProperty("noise_num")
    private final int noise;
}
