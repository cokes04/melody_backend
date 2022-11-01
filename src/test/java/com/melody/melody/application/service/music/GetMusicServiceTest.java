package com.melody.melody.application.service.music;

import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.melody.melody.domain.model.TestDomainGenerator.*;

@ExtendWith(SpringExtension.class)
class GetMusicServiceTest {
    @InjectMocks
    private GetMusicService service;

    @Mock
    private MusicRepository repository;

    @Test
    void execute_returnMusic() {
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