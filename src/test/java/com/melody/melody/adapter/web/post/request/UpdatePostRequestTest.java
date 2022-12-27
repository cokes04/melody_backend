package com.melody.melody.adapter.web.post.request;

import com.melody.melody.adapter.web.post.TestPostWebGenerator;
import com.melody.melody.application.service.post.UpdatePostService;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePostRequestTest {

    @Test
    void toCommand_ShuoldReturnCreatedCommand() {
        UpdatePostRequest request = TestPostWebGenerator.randomUpdatePostRequest();
        Identity postId = TestPostDomainGenerator.randomPostId();

        UpdatePostService.Command command = request.toCommand(postId.getValue());

        assertEquals(request.getTitle(), command.getTitle().get());
        assertEquals(request.getContent(), command.getContent().get());
        assertEquals(request.getOpen(), command.getOpen().get());
    }

    @Test
    void toCommand_ShuoldReturnCreatedEmptyTitleCommand_WhenNullTitle() {
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title(null)
                .content(TestPostDomainGenerator.randomContent().getValue())
                .open(true)
                .build();

        Identity postId = TestPostDomainGenerator.randomPostId();

        UpdatePostService.Command command = request.toCommand(postId.getValue());

        assertTrue(command.getTitle().isEmpty());
        assertEquals(request.getContent(), command.getContent().get());
        assertEquals(request.getOpen(), command.getOpen().get());
    }

    @Test
    void toCommand_ShuoldReturnCreatedEmptyContentCommand_WhenNullContent() {
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title(TestPostDomainGenerator.randomTitle().getValue())
                .content(null)
                .open(true)
                .build();

        Identity postId = TestPostDomainGenerator.randomPostId();

        UpdatePostService.Command command = request.toCommand(postId.getValue());

        assertEquals(request.getTitle(), command.getTitle().get());
        assertTrue(command.getContent().isEmpty());
        assertEquals(request.getOpen(), command.getOpen().get());
    }

    @Test
    void toCommand_ShuoldReturnCreatedEmptyOpenCommand_WhenNullOpen() {
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title(TestPostDomainGenerator.randomTitle().getValue())
                .content(TestPostDomainGenerator.randomContent().getValue())
                .open(null)
                .build();

        Identity postId = TestPostDomainGenerator.randomPostId();

        UpdatePostService.Command command = request.toCommand(postId.getValue());

        assertEquals(request.getTitle(), command.getTitle().get());
        assertEquals(request.getContent(), command.getContent().get());
        assertTrue(command.getOpen().isEmpty());
    }
}