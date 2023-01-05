package com.melody.melody.application.service.permissioncheck.post;

import com.melody.melody.adapter.security.*;
import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.application.service.post.CreatePostService;
import com.melody.melody.application.service.post.GetPostService;
import com.melody.melody.application.service.post.TestPostDetailServiceGenerator;
import com.melody.melody.domain.model.Identity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootTest(classes = {
        GetPostService.class,
        CustomMethodSecurityConfig.class,
        CustomMethodSecurityExpressionHandler.class,
        PostSecurityExpression.class
})
public class GetPostServicePermissionCheckTest {

    @Autowired
    private GetPostService service;

    @MockBean
    private PostDetailRepository postDetailRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private UserSecurityExpression userSecurityExpression;

    @MockBean
    private MusicSecurityExpression musicSecurityExpression;

    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenPostOwner() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(requesterId, false, false);
        Identity postId = Identity.from(postDetail.getId());

        when(postDetailRepository.findById(postId))
                .thenReturn(Optional.of(postDetail));

        GetPostService.Command command = new GetPostService.Command(postId.getValue());
        service.execute(command);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenNotPostOwnerAndOpenPost() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(requesterId / 13, true, false);
        Identity postId = Identity.from(postDetail.getId());

        when(postDetailRepository.findById(postId))
                .thenReturn(Optional.of(postDetail));

        GetPostService.Command command = new GetPostService.Command(postId.getValue());
        service.execute(command);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotPostOwnerAndClosePost() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(requesterId / 13, false, false);
        Identity postId = Identity.from(postDetail.getId());

        when(postDetailRepository.findById(postId))
                .thenReturn(Optional.of(postDetail));

        GetPostService.Command command = new GetPostService.Command(postId.getValue());
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);
    }
}
