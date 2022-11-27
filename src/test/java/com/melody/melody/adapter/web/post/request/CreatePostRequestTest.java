package com.melody.melody.adapter.web.post.request;

import com.melody.melody.adapter.web.post.TestPostWebGenerator;
import com.melody.melody.application.service.post.CreatePostService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePostRequestTest {

    @Test
    void to_ShouldReturnCreatedCommand() {
        CreatePostRequest request = TestPostWebGenerator.randomCreatePostRequest();
        long userId = 4301;

        CreatePostService.Command actual = request.to(userId);

        assertEquals(userId, actual.getUserId().getValue());
        assertEquals(request.getMusicId(), actual.getMusicId().getValue());
        assertEquals(request.getTitle(), actual.getTitle());
        assertEquals(request.getContent(), actual.getContent());
        assertEquals(request.getOpen(), actual.isOpen());

    }
}