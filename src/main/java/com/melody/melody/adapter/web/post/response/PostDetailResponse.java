package com.melody.melody.adapter.web.post.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.melody.melody.adapter.web.converter.BooleanToYNConverter;
import com.melody.melody.adapter.web.music.response.MusicResponse;
import com.melody.melody.adapter.web.user.response.WriterResponse;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class PostDetailResponse {
    private Long postId;

    private String title;

    private String content;

    private int likeCount;

    @JsonSerialize(converter = BooleanToYNConverter.class)
    private boolean open;

    @JsonSerialize(converter = BooleanToYNConverter.class)
    private boolean deleted;

    private LocalDateTime createdDate;

    private MusicResponse music;

    private WriterResponse writer;

}
