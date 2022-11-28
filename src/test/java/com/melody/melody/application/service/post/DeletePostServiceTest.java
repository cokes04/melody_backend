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
import static org.mockito.Mockito.*;

class DeletePostServiceTest {
    private DeletePostService service;
    private PostRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PostRepository.class);
        service = new DeletePostService(repository);
    }


    @Test
    void excute_ShouldReturnDeletedPost() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        Post.PostId postId = post.getId().get();
        DeletePostService.Command command = new DeletePostService.Command(postId);

        Post expect = TestPostDomainGenerator.clonePost(post);
        expect.delete();

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));
        when(repository.save(any(Post.class)))
                .thenAnswer( a -> a.getArgument(0, Post.class));

        DeletePostService.Result actual  = service.execute(command);

        assertEquals(expect, actual.getPost());

        verify(repository, times(1)).findById(postId);
        verify(repository, times(1)).save(any(Post.class));


    }

    @Test
    void excute_ShouldException_WhenPostNotFound() {
        Post.PostId postId = TestPostDomainGenerator.randomPostId();
        DeletePostService.Command command = new DeletePostService.Command(postId);

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