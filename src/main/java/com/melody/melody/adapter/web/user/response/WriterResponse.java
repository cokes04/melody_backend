package com.melody.melody.adapter.web.user.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WriterResponse {
    private String userId;

    private String nickname;
}
