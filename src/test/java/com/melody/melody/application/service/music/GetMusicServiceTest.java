package com.melody.melody.application.service.music;

import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.melody.melody.domain.model.TestMusicDomainGenerator.*;

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
        Music.MusicId id = randomMusicId();
        Music music = randomMusic();
        music = insertMusicId(music, id);
        Music expectedMusic = cloneMusic(music);
        GetMusicService.Command command = new GetMusicService.Command(id);


        when(repository.getById(eq(id)))
                .thenReturn(Optional.of(music));


        GetMusicService.Result result = service.execute(command);

        Music actualMusic;
        assertNotNull((actualMusic = result.getMusic()));
        assertEquals(expectedMusic, actualMusic);

        verify(repository, times(1)).getById(id);
    }

}