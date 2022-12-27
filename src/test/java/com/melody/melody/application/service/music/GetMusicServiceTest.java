package com.melody.melody.application.service.music;

import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetMusicServiceTest {
    private GetMusicService service;
    private MusicRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(MusicRepository.class);
        service = new GetMusicService(repository);
    }

    @Test
    void execute_ShouldReturnMusic() {
        Identity musicId = TestMusicDomainGenerator.randomMusicId();
        Music music = TestMusicDomainGenerator.randomMusic();
        music = TestMusicDomainGenerator.insertMusicId(music, musicId);
        Music expectedMusic = TestMusicDomainGenerator.cloneMusic(music);
        GetMusicService.Command command = new GetMusicService.Command(musicId.getValue());


        when(repository.findById(eq(musicId)))
                .thenReturn(Optional.of(music));


        GetMusicService.Result result = service.execute(command);

        Music actualMusic;
        assertNotNull((actualMusic = result.getMusic()));
        assertEquals(expectedMusic, actualMusic);

        verify(repository, times(1)).findById(musicId);
    }

}