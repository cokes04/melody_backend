package com.melody.melody.application.service.permissioncheck;

import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.port.out.*;
import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.application.service.music.TestMusicServiceGenerator;
import com.melody.melody.application.service.post.CreatePostService;
import com.melody.melody.application.service.post.TestPostServiceGenerator;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import java.net.MalformedURLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GenerateMusicServicePermissionCheckTest {

    @Autowired
    private GenerateMusicService service;

    @MockBean
    private ImageFileStorage imageFileStorage;
    @MockBean
    private ImageCaptioner imageCaptioner;
    @MockBean
    private EmotionClassifier emotionClassifier;
    @MockBean
    private MusicGenerator musicGenerator;
    @MockBean
    private MusicRepository musicRepository;



    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenUserSelf() throws MalformedURLException {
        User.UserId userId = new User.UserId(requesterId);

        when(musicRepository.save(any(Music.class)))
                .thenReturn(TestMusicDomainGenerator.randomCompletionMusic(userId));

        GenerateMusicService.Command command = TestMusicServiceGenerator.randomGenerateMusicCommand(userId);
        service.execute(command);

        verify(musicRepository, times(1))
                .save(any(Music.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotUserSelf() throws MalformedURLException {
        User.UserId userId = new User.UserId(requesterId / 31);

        GenerateMusicService.Command command = TestMusicServiceGenerator.randomGenerateMusicCommand(userId);
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(musicRepository, times(0))
                .save(any(Music.class));
    }
}
