package com.melody.melody.application.dto;

import com.melody.melody.domain.model.Music;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class PostDetail {

    private Long id;

    private String title;

    private String content;

    private int likeCount;

    private boolean open;

    private boolean deleted;

    private LocalDateTime createdDate;


    private Long userId;

    private String nickname;


    private Long musicId;

    private Music.Emotion emotion;

    private String explanation;

    private String imageUrl;

    private String musicUrl;

    private Music.Status musicStatus;

}