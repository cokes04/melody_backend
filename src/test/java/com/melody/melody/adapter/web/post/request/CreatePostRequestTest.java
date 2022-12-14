package com.melody.melody.adapter.web.post.request;

import com.melody.melody.adapter.web.post.TestPostWebGenerator;
import com.melody.melody.application.service.post.CreatePostService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePostRequestTest {

    @Test
    void toCommand_ShouldReturnCreatedCommand() {
        CreatePostRequest request = TestPostWebGenerator.randomCreatePostRequest();
        long userId = 4301;

        CreatePostService.Command actual = request.toCommand(userId);

        assertEquals(userId, actual.getUserId());
        assertEquals(request.getMusicId(), actual.getMusicId());
        assertEquals(request.getTitle(), actual.getTitle());
        assertEquals(request.getContent(), actual.getContent());
        assertEquals(request.getOpen(), actual.isOpen());

    }
}