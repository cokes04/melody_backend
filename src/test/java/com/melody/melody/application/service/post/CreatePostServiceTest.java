package com.melody.melody.application.service.post;

import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreatePostServiceTest {
    private CreatePostService service;
    private PostRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PostRepository.class);
        service = new CreatePostService(repository);
    }

    @Test
    void create_ShuoldReturnCreatedPost() {
        CreatePostService.Command command = TestPostServiceGenerator.randomCreatePostCommand();
        Post expect = TestPostDomainGenerator.randomOpenPost();

        when(repository.save(any(Post.class)))
                .thenReturn(expect);

        CreatePostService.Result actual = service.execute(command);

        assertEquals(expect, actual.getPost());

        verify(repository, times(1)).save(any(Post.class));
    }
}