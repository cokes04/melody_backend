package com.melody.melody.adapter.aws.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class LambdaEmotionClassifitionResponse {
    private String emotion;
}
