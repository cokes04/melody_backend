package com.melody.melody.application.service.permissioncheck.post;

import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.application.service.post.GetPostService;
import com.melody.melody.application.service.post.TestPostDetailServiceGenerator;
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
import static org.mockito.Mockito.when;

@SpringBootTest
public class GetPostServicePermissionCheckTest {

    @Autowired
    private GetPostService service;

    @MockBean
    private PostDetailRepository postDetailRepository;

    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenPostOwner() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(requesterId, false, false);
        Post.PostId postId = new Post.PostId(postDetail.getId());

        when(postDetailRepository.findById(postId))
                .thenReturn(Optional.of(postDetail));

        GetPostService.Command command = new GetPostService.Command(postId);
        service.execute(command);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenNotPostOwnerAndOpenPost() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(requesterId / 13, true, false);
        Post.PostId postId = new Post.PostId(postDetail.getId());

        when(postDetailRepository.findById(postId))
                .thenReturn(Optional.of(postDetail));

        GetPostService.Command command = new GetPostService.Command(postId);
        service.execute(command);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotPostOwnerAndClosePost() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail(requesterId / 13, false, false);
        Post.PostId postId = new Post.PostId(postDetail.getId());

        when(postDetailRepository.findById(postId))
                .thenReturn(Optional.of(postDetail));

        GetPostService.Command command = new GetPostService.Command(postId);
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);
    }
}
