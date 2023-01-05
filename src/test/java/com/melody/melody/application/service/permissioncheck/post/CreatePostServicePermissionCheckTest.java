package com.melody.melody.application.service.permissioncheck.post;

import com.melody.melody.adapter.security.*;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.application.service.post.CreatePostService;
import com.melody.melody.application.service.post.TestPostServiceGenerator;
import com.melody.melody.application.service.user.UpdateUserService;
import com.melody.melody.domain.model.*;
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
        CreatePostService.class,
        CustomMethodSecurityConfig.class,
        CustomMethodSecurityExpressionHandler.class,
        UserSecurityExpression.class,
        MusicSecurityExpression.class
})
public class CreatePostServicePermissionCheckTest {

    @Autowired
    private CreatePostService service;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private MusicRepository musicRepository;

    @MockBean
    private PostSecurityExpression notNeeded;

    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenMusicExsist_AndMusicOwner_AndReqeusterIsMe() {
        Identity userId = Identity.from(requesterId);
        Music music = TestMusicDomainGenerator.randomCompletionMusic(userId);
        Identity musicId = music.getId();

        when(musicRepository.findById(musicId))
                .thenReturn(Optional.of(music));

        CreatePostService.Command command = TestPostServiceGenerator.randomCreatePostCommand(userId.getValue(), musicId.getValue());
        service.execute(command);

        verify(musicRepository, times(2))
                .findById(any(Identity.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotExsistMusic() {
        when(musicRepository.findById(any(Identity.class)))
                .thenReturn(Optional.empty());

        CreatePostService.Command command = TestPostServiceGenerator.randomCreatePostCommand(requesterId, 239829L);
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);


        verify(musicRepository, times(1))
                .findById(any(Identity.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotMusicOwner() {
        Music music = TestMusicDomainGenerator.randomCompletionMusic(Identity.from((requesterId / 13) * 3));
        Identity musicId = music.getId();

        when(musicRepository.findById(musicId))
                .thenReturn(Optional.of(music));

        CreatePostService.Command command = TestPostServiceGenerator.randomCreatePostCommand(requesterId, musicId.getValue());

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(musicRepository, times(2))
                .findById(any(Identity.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenReqeusterIsNotMe() {
        Identity userId = Identity.from(requesterId);
        Music music = TestMusicDomainGenerator.randomCompletionMusic(userId);
        Identity musicId = music.getId();

        when(musicRepository.findById(musicId))
                .thenReturn(Optional.of(music));

        CreatePostService.Command command = TestPostServiceGenerator.randomCreatePostCommand(requesterId / 13, musicId.getValue());
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(musicRepository, times(2))
                .findById(any(Identity.class));
    }
}
