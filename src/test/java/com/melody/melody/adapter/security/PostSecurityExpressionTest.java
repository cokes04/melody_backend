package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PostSecurityExpressionTest {
    private PostSecurityExpression expression;

    private PostRepository repository;


    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PostRepository.class);
        expression = new PostSecurityExpression(repository);
    }

    @Test
    void isOwner_ShouldReturnTrue_WhenPostOwner() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        User.UserId userId = post.getUserId();
        Post.PostId postId = post.getId().get();

        setAuthentication(userId.getValue());

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));

        boolean actual = expression.isOwner(postId);
        assertTrue(actual);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenOtherUser() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        User.UserId userId = post.getUserId();
        Post.PostId postId = post.getId().get();

        long otherUserId = userId.getValue() + 322312;
        setAuthentication(otherUserId);

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));

        boolean actual = expression.isOwner(postId);
        assertFalse(actual);
    }

    @Test
    void isOwner_ShouldReturnTrue_WhenNotFoundPost() {
        Post.PostId postId = TestPostDomainGenerator.randomPostId();

        when(repository.findById(postId))
                .thenReturn(Optional.empty());

        boolean actual = expression.isOwner(postId);
        assertTrue(actual);

    }

    void setAuthentication(long userId){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserDetailsImpl.builder().userId(userId).build(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        expression.setAuthentication(authentication);
    }
}