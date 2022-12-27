package com.melody.melody.application.service.permissioncheck.music;

import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.port.out.*;
import com.melody.melody.application.service.music.GetMusicService;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GetMusicServicePermissionCheckTest {

    @Autowired
    private GetMusicService service;

    @MockBean
    private MusicRepository musicRepository;


    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenUserSelf() {
        User.UserId userId = new User.UserId(requesterId);
        Music music = TestMusicDomainGenerator.randomCompletionMusic(userId);


        when(musicRepository.findById(music.getId().get()))
                .thenReturn(Optional.of(music));

        GetMusicService.Command command = new GetMusicService.Command(music.getId().get());
        service.execute(command);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotUserSelf() {
        User.UserId userId = new User.UserId((requesterId / 13) + 37);
        Music music = TestMusicDomainGenerator.randomCompletionMusic(userId);


        when(musicRepository.findById(music.getId().get()))
                .thenReturn(Optional.of(music));

        GetMusicService.Command command = new GetMusicService.Command(music.getId().get());
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

    }
}
