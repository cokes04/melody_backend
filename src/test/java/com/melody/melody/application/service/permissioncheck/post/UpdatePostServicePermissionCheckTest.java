package com.melody.melody.application.service.permissioncheck.post;

import com.melody.melody.adapter.security.*;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.application.service.post.GetPostService;
import com.melody.melody.application.service.post.TestPostServiceGenerator;
import com.melody.melody.application.service.post.UpdatePostService;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootTest(classes = {
        UpdatePostService.class,
        CustomMethodSecurityConfig.class,
        CustomMethodSecurityExpressionHandler.class,
        PostSecurityExpression.class
})
public class UpdatePostServicePermissionCheckTest {

    @Autowired
    private UpdatePostService service;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private UserSecurityExpression userSecurityExpression;

    @MockBean
    private MusicSecurityExpression musicSecurityExpression;

    private final long requesterId = 53245;


    @Test
    @WithMockRequester(userId = requesterId)
    void excute_SholudPass_WhenPostOwner() {
        Post post = TestPostDomainGenerator.randomOpenPost(Identity.from(requesterId));

        when(postRepository.findById(post.getId()))
                .thenReturn(Optional.of(post));

        UpdatePostService.Command command = TestPostServiceGenerator.randomUpdatePostService(post.getId().getValue());
        service.execute(command);

        verify(postRepository, times(2))
                .findById(any(Identity.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_SholudPass_WhenNotExistsPost() {
        when(postRepository.findById(any(Identity.class)))
                .thenReturn(Optional.empty());

        UpdatePostService.Command command = TestPostServiceGenerator.randomUpdatePostService(143289);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(NotFoundException.class);

        verify(postRepository, times(2))
                .findById(any(Identity.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_SholudBlock_WhenNotPostOwner() {
        Post post = TestPostDomainGenerator.randomOpenPost(Identity.from((requesterId / 13) * 3));

        when(postRepository.findById(post.getId()))
                .thenReturn(Optional.of(post));

        UpdatePostService.Command command = TestPostServiceGenerator.randomUpdatePostService(post.getId().getValue());

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(postRepository, times(1))
                .findById(any(Identity.class));
    }

}
