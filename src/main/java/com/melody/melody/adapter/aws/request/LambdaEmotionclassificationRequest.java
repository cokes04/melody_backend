package com.melody.melody.adapter.aws.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LambdaEmotionclassificationRequest {
    @JsonProperty("caption")
    private final String caption;
}
