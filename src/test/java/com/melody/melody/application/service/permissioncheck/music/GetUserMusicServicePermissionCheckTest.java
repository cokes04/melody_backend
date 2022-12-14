package com.melody.melody.application.service.permissioncheck.music;

import com.melody.melody.adapter.security.*;
import com.melody.melody.application.dto.MusicPublish;
import com.melody.melody.application.dto.MusicSort;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.application.service.music.GetUserMusicService;
import com.melody.melody.application.service.post.CreatePostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootTest(classes = {
        GetUserMusicService.class,
        CustomMethodSecurityConfig.class,
        CustomMethodSecurityExpressionHandler.class,
        UserSecurityExpression.class
})
public class GetUserMusicServicePermissionCheckTest {

    @Autowired
    private GetUserMusicService service;

    @MockBean
    private MusicRepository musicRepository;

    @MockBean
    private MusicSecurityExpression musicSecurityExpression;

    @MockBean
    private PostSecurityExpression postSecurityExpression;

    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenUserSelf(){
        MusicPublish musicPublish = MusicPublish.Everything;
        PagingInfo<MusicSort> musicPaging = new PagingInfo<>(0, 20, MusicSort.newest);

        GetUserMusicService.Command command = new GetUserMusicService.Command(requesterId, musicPublish, musicPaging);
        service.execute(command);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotUserSelf(){
        long otherRequesterId = (requesterId / 13) + 37;
        MusicPublish musicPublish = MusicPublish.Everything;
        PagingInfo<MusicSort> musicPaging = new PagingInfo<>(0, 20, MusicSort.newest);

        GetUserMusicService.Command command = new GetUserMusicService.Command(otherRequesterId, musicPublish, musicPaging);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

    }
}
