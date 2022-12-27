package com.melody.melody.application.service.permissioncheck.post;

import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.application.service.post.DeletePostService;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DeletePostServicePermissionCheckTest {

    @Autowired
    private DeletePostService service;

    @MockBean
    private PostRepository postRepository;


    private final long requesterId = 53245;


    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenPostOwner() {
        Post post = TestPostDomainGenerator.randomOpenPost(new User.UserId(requesterId));

        when(postRepository.findById(post.getId().get()))
                .thenReturn(Optional.of(post));

        DeletePostService.Command command = new DeletePostService.Command(post.getId().get());
        service.execute(command);

        verify(postRepository, times(2))
                .findById(any(Post.PostId.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenNotExistsPost() {
        when(postRepository.findById(any(Post.PostId.class)))
                .thenReturn(Optional.empty());

        DeletePostService.Command command = new DeletePostService.Command(new Post.PostId(4102));

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(NotFoundException.class);

        verify(postRepository, times(2))
                .findById(any(Post.PostId.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotPostOwner() {
        Post post = TestPostDomainGenerator.randomOpenPost(new User.UserId((requesterId / 13) * 3));

        when(postRepository.findById(post.getId().get()))
                .thenReturn(Optional.of(post));

        DeletePostService.Command command = new DeletePostService.Command(post.getId().get());

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(postRepository, times(1))
                .findById(any(Post.PostId.class));
    }
}
