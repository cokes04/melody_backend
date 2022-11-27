package com.melody.melody.adapter.web.post.request;

import com.melody.melody.adapter.web.post.TestPostWebGenerator;
import com.melody.melody.application.service.post.UpdatePostService;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePostRequestTest {

    @Test
    void to_ShuoldReturnCreatedCommand() {
        UpdatePostRequest request = TestPostWebGenerator.randomUpdatePostRequest();
        Post.PostId postId = TestPostDomainGenerator.randomPostId();

        UpdatePostService.Command command = request.to(postId);

        assertEquals(request.getTitle(), command.getTitle().get());
        assertEquals(request.getContent(), command.getContent().get());
        assertEquals(request.getOpen(), command.getOpen().get());
    }

    @Test
    void to_ShuoldReturnCreatedEmptyTitleCommand_WhenNullTitle() {
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title(null)
                .content(TestPostDomainGenerator.randomContent().getValue())
                .open(true)
                .build();

        Post.PostId postId = TestPostDomainGenerator.randomPostId();

        UpdatePostService.Command command = request.to(postId);

        assertTrue(command.getTitle().isEmpty());
        assertEquals(request.getContent(), command.getContent().get());
        assertEquals(request.getOpen(), command.getOpen().get());
    }

    @Test
    void to_ShuoldReturnCreatedEmptyContentCommand_WhenNullContent() {
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title(TestPostDomainGenerator.randomTitle().getValue())
                .content(null)
                .open(true)
                .build();

        Post.PostId postId = TestPostDomainGenerator.randomPostId();

        UpdatePostService.Command command = request.to(postId);

        assertEquals(request.getTitle(), command.getTitle().get());
        assertTrue(command.getContent().isEmpty());
        assertEquals(request.getOpen(), command.getOpen().get());
    }

    @Test
    void to_ShuoldReturnCreatedEmptyOpenCommand_WhenNullOpen() {
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title(TestPostDomainGenerator.randomTitle().getValue())
                .content(TestPostDomainGenerator.randomContent().getValue())
                .open(null)
                .build();

        Post.PostId postId = TestPostDomainGenerator.randomPostId();

        UpdatePostService.Command command = request.to(postId);

        assertEquals(request.getTitle(), command.getTitle().get());
        assertEquals(request.getContent(), command.getContent().get());
        assertTrue(command.getOpen().isEmpty());
    }
}