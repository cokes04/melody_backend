package com.melody.melody.adapter.web.post.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.melody.melody.adapter.web.converter.YNToBooleanConverter;
import com.melody.melody.application.service.post.CreatePostService;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequest {
    @Positive @NotNull
    private long musicId;

    @NotBlank
    private String title;

    private String content;

    @JsonDeserialize(converter = YNToBooleanConverter.class)
    private Boolean open;

    public CreatePostService.Command toCommand(long userId){
        return new CreatePostService.Command(
                userId,
                musicId,
                title,
                content,
                open
        );
    }
}