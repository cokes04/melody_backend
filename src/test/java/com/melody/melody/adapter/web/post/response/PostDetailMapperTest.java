package com.melody.melody.adapter.web.post.response;

import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.application.service.post.TestPostDetailServiceGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostDetailMapperTest {
    @Test
    void to_ShouldReturnResponse() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail();
        PostDetailResponse response = PostDetailMapper.to(postDetail);

        assertEquals(postDetail.getId(), response.getPostId());
        assertEquals(postDetail.getTitle(), response.getTitle());
        assertEquals(postDetail.getContent(), response.getContent());
        assertEquals(postDetail.getLikeCount(), response.getLikeCount());
        assertEquals(postDetail.isDeleted(), response.isDeleted());
        assertEquals(postDetail.isOpen(), response.isOpen());
        assertEquals(postDetail.getCreatedDate(), response.getCreatedDate());

        assertEquals(postDetail.getMusicId(), response.getMusic().getMusicId());
        assertEquals(postDetail.getUserId(), response.getMusic().getUserId());
        assertEquals(postDetail.getEmotion(), response.getMusic().getEmotion());
        assertEquals(postDetail.getExplanation(), response.getMusic().getExplanation());
        assertEquals(postDetail.getMusicStatus(), response.getMusic().getStatus());
        assertEquals(postDetail.getImageUrl(), response.getMusic().getImageUrl());
        assertEquals(postDetail.getMusicUrl(), response.getMusic().getMusicUrl());

        assertEquals(postDetail.getUserId(), response.getWriter().getUserId());
        assertEquals(postDetail.getNickname(), response.getWriter().getNickname());
    }
}