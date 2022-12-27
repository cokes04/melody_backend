package com.melody.melody.adapter.web.post.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.melody.melody.adapter.web.converter.YNToBooleanConverter;
import com.melody.melody.application.service.post.UpdatePostService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePostRequest {

    private String title;

    private String content;

    @JsonDeserialize(converter = YNToBooleanConverter.class)
    private Boolean open;

    public UpdatePostService.Command toCommand(long postId){
        return new UpdatePostService.Command(
                postId,
                title,
                content,
                open
        );
    }
}
