package com.melody.melody.adapter.persistence.postdetail;

import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public class PostDetailData extends PostDetail {

    @QueryProjection

    public PostDetailData(Long id, String title, String content, int likeCount, boolean open, boolean deleted, LocalDateTime createdDate,
                          Long userId, String nickname, Long musicId, Emotion emotion, String explanation, String imageUrl, String musicUrl, Music.Status musicStatus) {
        super(id, title, content, likeCount, open, deleted, createdDate, userId, nickname, musicId, emotion, explanation, imageUrl, musicUrl, musicStatus);
    }
}
