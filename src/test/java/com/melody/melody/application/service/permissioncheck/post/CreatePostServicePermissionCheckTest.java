package com.melody.melody.application.service.permissioncheck.post;

import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.application.service.post.CreatePostService;
import com.melody.melody.application.service.post.TestPostServiceGenerator;
import com.melody.melody.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CreatePostServicePermissionCheckTest {

    @Autowired
    private CreatePostService service;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private MusicRepository musicRepository;


    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenMusicExsist_AndMusicOwner_AndReqeusterIsMe() {
        User.UserId userId = new User.UserId(requesterId);
        Music music = TestMusicDomainGenerator.randomCompletionMusic(userId);
        Music.MusicId musicId = music.getId().get();

        when(musicRepository.findById(musicId))
                .thenReturn(Optional.of(music));

        CreatePostService.Command command = TestPostServiceGenerator.randomCreatePostCommand(userId, musicId);
        service.execute(command);

        verify(musicRepository, times(2))
                .findById(any(Music.MusicId.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotExsistMusic() {
        User.UserId userId = new User.UserId(requesterId);

        when(musicRepository.findById(any(Music.MusicId.class)))
                .thenReturn(Optional.empty());

        CreatePostService.Command command = TestPostServiceGenerator.randomCreatePostCommand(userId, new Music.MusicId(239829L));
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);


        verify(musicRepository, times(1))
                .findById(any(Music.MusicId.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotMusicOwner() {
        User.UserId userId = new User.UserId(requesterId);
        Music music = TestMusicDomainGenerator.randomCompletionMusic(new User.UserId((requesterId / 13) * 3));
        Music.MusicId musicId = music.getId().get();

        when(musicRepository.findById(musicId))
                .thenReturn(Optional.of(music));

        CreatePostService.Command command = TestPostServiceGenerator.randomCreatePostCommand(userId, musicId);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(musicRepository, times(2))
                .findById(any(Music.MusicId.class));
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenReqeusterIsNotMe() {
        User.UserId userId = new User.UserId(requesterId);
        Music music = TestMusicDomainGenerator.randomCompletionMusic(userId);
        Music.MusicId musicId = music.getId().get();

        when(musicRepository.findById(musicId))
                .thenReturn(Optional.of(music));

        CreatePostService.Command command = TestPostServiceGenerator.randomCreatePostCommand(new User.UserId(requesterId / 13), musicId);
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(musicRepository, times(2))
                .findById(any(Music.MusicId.class));
    }
}
