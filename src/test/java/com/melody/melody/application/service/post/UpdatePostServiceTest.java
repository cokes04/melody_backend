package com.melody.melody.application.service.post;

import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UpdatePostServiceTest {
    private UpdatePostService service;
    private PostRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PostRepository.class);
        service = new UpdatePostService(repository);
    }

    @Test
    void excute_ShouldReturnUpdatedPost() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        Post.PostId postId = post.getId().get();

        Post.Title expectTitle = TestPostDomainGenerator.randomTitle();
        Post.Content expectContent = TestPostDomainGenerator.randomContent();
        UpdatePostService.Command command = new UpdatePostService.Command(postId, expectTitle.getValue(), expectContent.getValue());


        when(repository.findById(postId))
                .thenReturn(Optional.of(post));

        UpdatePostService.Result actual = service.execute(command);

        assertEquals(expectTitle, actual.getPost().getTitle());
        assertEquals(expectContent, actual.getPost().getContent());
    }

    @Test
    void excute_ShouldException_WhenPostNotFound() {
        Post.PostId postId = TestPostDomainGenerator.randomPostId();

        String title = TestPostDomainGenerator.randomTitle().getValue();
        String content = TestPostDomainGenerator.randomContent().getValue();
        UpdatePostService.Command command = new UpdatePostService.Command(postId, title, content);

        when(repository.findById(postId))
                .thenReturn(Optional.empty());

        assertException(
                () -> service.execute(command),
                NotFoundException.class,
                DomainError.of(PostErrorType.Not_Found_Post)
        );
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}