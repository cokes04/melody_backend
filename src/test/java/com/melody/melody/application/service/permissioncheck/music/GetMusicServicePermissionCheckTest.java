package com.melody.melody.application.service.permissioncheck.music;

import com.melody.melody.adapter.security.*;
import com.melody.melody.application.port.out.*;
import com.melody.melody.application.service.music.GetMusicService;
import com.melody.melody.application.service.music.GetUserMusicService;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootTest(classes = {
        GetMusicService.class,
        CustomMethodSecurityConfig.class,
        CustomMethodSecurityExpressionHandler.class,
        MusicSecurityExpression.class
})
public class GetMusicServicePermissionCheckTest {

    @Autowired
    private GetMusicService service;

    @MockBean
    private MusicRepository musicRepository;

    @MockBean
    private UserSecurityExpression userSecurityExpression;

    @MockBean
    private PostSecurityExpression postSecurityExpression;

    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenUserSelf() {
        Identity userId = Identity.from(requesterId);
        Music music = TestMusicDomainGenerator.randomCompletionMusic(userId);


        when(musicRepository.findById(music.getId()))
                .thenReturn(Optional.of(music));

        GetMusicService.Command command = new GetMusicService.Command(music.getId().getValue());
        service.execute(command);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotUserSelf() {
        Identity userId = Identity.from((requesterId / 13) + 37);
        Music music = TestMusicDomainGenerator.randomCompletionMusic(userId);


        when(musicRepository.findById(music.getId()))
                .thenReturn(Optional.of(music));

        GetMusicService.Command command = new GetMusicService.Command(music.getId().getValue());
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

    }
}
