package com.melody.melody.adapter.web.request;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Value
@Builder
public class MusicRequest {
    @Positive @NotNull
    private final int musicLength;

    @Positive @NotNull
    private final int noise;
}
