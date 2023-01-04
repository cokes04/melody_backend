package com.melody.melody.application.service.permissioncheck.post;

import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.application.service.post.GetUserPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class GetUserPostServicePermissionCheckTest {

    @Autowired
    private GetUserPostService service;

    @MockBean
    private PostDetailRepository postDetailRepository;

    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenPostOwner() {
        PagingInfo<PostSort> pagingInfo = new PagingInfo<>(0, 10, PostSort.oldest);

        GetUserPostService.Command command = new GetUserPostService.Command(requesterId, Open.Everything, pagingInfo);
        service.execute(command);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenNotPostOwnerAndOpenPost() {
        PagingInfo<PostSort> pagingInfo = new PagingInfo<>(0, 10, PostSort.oldest);

        GetUserPostService.Command command = new GetUserPostService.Command((requesterId / 13) + 37, Open.OnlyOpen, pagingInfo);
        service.execute(command);

    }


    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotPostOwnerAndEverytionPost() {
        PagingInfo<PostSort> pagingInfo = new PagingInfo<>(0, 10, PostSort.oldest);

        GetUserPostService.Command command = new GetUserPostService.Command((requesterId / 13) + 37, Open.Everything, pagingInfo);
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotPostOwnerAndClosePost() {
        PagingInfo<PostSort> pagingInfo = new PagingInfo<>(0, 10, PostSort.oldest);

        GetUserPostService.Command command = new GetUserPostService.Command((requesterId / 13) + 37, Open.OnlyClose, pagingInfo);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);
    }
}