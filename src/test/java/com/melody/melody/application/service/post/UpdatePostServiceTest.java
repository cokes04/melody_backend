package com.melody.melody.application.service.post;

import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        Identity postId = post.getId();

        Post.Title expectTitle = TestPostDomainGenerator.randomTitle();
        Post.Content expectContent = TestPostDomainGenerator.randomContent();
        boolean open = false;
        UpdatePostService.Command command = new UpdatePostService.Command(postId.getValue(), expectTitle.getValue(), expectContent.getValue(), open);

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));

        when(repository.save(any(Post.class)))
                .thenAnswer( a -> a.getArgument(0, Post.class));

        UpdatePostService.Result actual = service.execute(command);

        assertEquals(expectTitle, actual.getPost().getTitle());
        assertEquals(expectContent, actual.getPost().getContent());
        assertFalse(actual.getPost().isOpen());

        verify(repository, times(1)).findById(postId);
        verify(repository, times(1)).save(any(Post.class));
    }

    @Test
    void excute_ShouldException_WhenPostNotFound() {
        Identity postId = TestPostDomainGenerator.randomPostId();

        String title = TestPostDomainGenerator.randomTitle().getValue();
        String content = TestPostDomainGenerator.randomContent().getValue();
        boolean open = false;
        UpdatePostService.Command command = new UpdatePostService.Command(postId.getValue(), title, content, open);

        when(repository.findById(postId))
                .thenReturn(Optional.empty());

        assertException(
                () -> service.execute(command),
                NotFoundException.class,
                DomainError.of(PostErrorType.Not_Found_Post)
        );

        verify(repository, times(1)).findById(postId);
    }

    @Test
    void excute_ShouldNotChangeTitle_WhenEmptyTitle() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        Identity postId = post.getId();

        Post.Title expectTitle = new Post.Title(post.getTitle().getValue());
        Post.Content expectContent = TestPostDomainGenerator.randomContent();
        boolean open = false;
        UpdatePostService.Command command = new UpdatePostService.Command(postId.getValue(), null, expectContent.getValue(), open);

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));
        when(repository.save(any(Post.class)))
                .thenAnswer( a -> a.getArgument(0, Post.class));

        UpdatePostService.Result actual = service.execute(command);

        assertEquals(expectTitle, actual.getPost().getTitle());
        assertEquals(expectContent, actual.getPost().getContent());
        assertFalse(actual.getPost().isOpen());

        verify(repository, times(1)).findById(postId);
        verify(repository, times(1)).save(any(Post.class));
    }

    @Test
    void excute_ShouldNotChangeContent_WhenEmptyContent() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        Identity postId = post.getId();

        Post.Title expectTitle = TestPostDomainGenerator.randomTitle();
        Post.Content expectContent = new Post.Content(post.getContent().getValue());
        boolean open = false;
        UpdatePostService.Command command = new UpdatePostService.Command(postId.getValue(), expectTitle.getValue(), null, open);

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));
        when(repository.save(any(Post.class)))
                .thenAnswer( a -> a.getArgument(0, Post.class));

        UpdatePostService.Result actual = service.execute(command);

        assertEquals(expectTitle, actual.getPost().getTitle());
        assertEquals(expectContent, actual.getPost().getContent());
        assertFalse(actual.getPost().isOpen());

        verify(repository, times(1)).findById(postId);
        verify(repository, times(1)).save(any(Post.class));
    }

    @Test
    void excute_ShouldNotChangeOpen_WhenEmptyOpen() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        Identity postId = post.getId();

        Post.Title expectTitle = TestPostDomainGenerator.randomTitle();
        Post.Content expectContent = TestPostDomainGenerator.randomContent();
        UpdatePostService.Command command = new UpdatePostService.Command(postId.getValue(), expectTitle.getValue(), expectContent.getValue(), null);

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));
        when(repository.save(any(Post.class)))
                .thenAnswer( a -> a.getArgument(0, Post.class));

        UpdatePostService.Result actual = service.execute(command);

        assertEquals(expectTitle, actual.getPost().getTitle());
        assertEquals(expectContent, actual.getPost().getContent());
        assertTrue(actual.getPost().isOpen());

        verify(repository, times(1)).findById(postId);
        verify(repository, times(1)).save(any(Post.class));
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}