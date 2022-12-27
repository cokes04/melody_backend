package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.application.service.post.TestPostDetailServiceGenerator;
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
        Identity userId = post.getUserId();
        Identity postId = post.getId();

        setAuthentication(userId.getValue());

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));

        boolean actual = expression.isOwner(postId);
        assertTrue(actual);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenOtherUser() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        Identity userId = post.getUserId();
        Identity postId = post.getId();

        long otherUserId = userId.getValue() + 322312;
        setAuthentication(otherUserId);

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));

        boolean actual = expression.isOwner(postId);
        assertFalse(actual);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenNullAuthentication() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        Identity postId = post.getId();

        when(repository.findById(postId))
                .thenReturn(Optional.of(post));

        expression.setAuthentication(null);

        boolean actual = expression.isOwner(postId);
        assertFalse(actual);
    }

    @Test
    void isOwner_ShouldReturnTrue_WhenNotFoundPost() {
        Identity postId = TestPostDomainGenerator.randomPostId();

        when(repository.findById(postId))
                .thenReturn(Optional.empty());

        boolean actual = expression.isOwner(postId);
        assertTrue(actual);

    }

    @Test
    void isOwner_PostDetail_ShouldReturnTrue_WhenPostDetailOwner() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(40, true, false);

        setAuthentication(postDetail.getUserId());

        boolean actual = expression.isOwner(postDetail);
        assertTrue(actual);
    }

    @Test
    void isOwner_PostDetail_ShouldReturnFalse_WhenNotPostDetailOwner() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(40, true, false);

        setAuthentication(postDetail.getUserId() / 3);

        boolean actual = expression.isOwner(postDetail);
        assertFalse(actual);
    }

    @Test
    void isOwner_PostDetail_ShouldReturnFalse_WhenNullAuthentication() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(40, true, false);

        expression.setAuthentication(null);
        boolean actual = expression.isOwner(postDetail);
        assertFalse(actual);
    }

    @Test
    void isOpen_PostDetail_ShouldReturnTrue_WhenOpenPostDetail() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(40, true, false);

        boolean actual = expression.isOpen(postDetail);
        assertTrue(actual);
    }

    @Test
    void isOpen_PostDetail_ShouldReturnTrue_WhenClosePostDetail() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(40, false, false);

        boolean actual = expression.isOpen(postDetail);
        assertFalse(actual);
    }
    
    @Test
    void isOpen_Open_ShouldReturnTrue_WhenOnlyOpen() {
        Open open = Open.OnlyOpen;

        boolean actual = expression.isOpen(open);
        assertTrue(actual);
    }

    @Test
    void isOpen_Open_ShouldReturnFalse_WhenEverything() {
        Open open = Open.Everything;

        boolean actual = expression.isOpen(open);
        assertFalse(actual);
    }

    @Test
    void isOpen_Open_ShouldReturnFalse_WhenOnlyClose() {
        Open open = Open.OnlyClose;

        boolean actual = expression.isOpen(open);
        assertFalse(actual);
    }

    void setAuthentication(long userId){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserDetailsImpl.builder().userId(userId).build(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        expression.setAuthentication(authentication);
    }
}