package com.melody.melody.adapter.aws.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LambdaImageCaptioningRequest {
    @JsonProperty("imageUri")
    private final String imageUri;
}
