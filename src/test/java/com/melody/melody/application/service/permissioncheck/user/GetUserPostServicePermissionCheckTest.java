package com.melody.melody.application.service.permissioncheck.user;

import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.application.service.post.GetUserPostService;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        User.UserId userId = new User.UserId(requesterId);

        GetUserPostService.Command command = new GetUserPostService.Command(userId, Open.Everything, new PagingInfo(0, 2, PostSort.newest));
        service.execute(command);

        verify(postDetailRepository, times(1))
                .findByUserId(any(User.UserId.class), any(Open.class), any(PagingInfo.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenNotPostOwnerAndOpenPost() {
        User.UserId userId = new User.UserId(requesterId / 13);

        GetUserPostService.Command command = new GetUserPostService.Command(userId, Open.OnlyOpen, new PagingInfo(0, 2, PostSort.newest));
        service.execute(command);

        verify(postDetailRepository, times(1))
                .findByUserId(any(User.UserId.class), any(Open.class), any(PagingInfo.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotPostOwnerAndClosePost() {
        User.UserId userId = new User.UserId(requesterId / 13);

        GetUserPostService.Command command = new GetUserPostService.Command(userId, Open.OnlyClose, new PagingInfo(0, 2, PostSort.newest));
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(postDetailRepository, times(0))
                .findByUserId(any(User.UserId.class), any(Open.class), any(PagingInfo.class));
    }
}
