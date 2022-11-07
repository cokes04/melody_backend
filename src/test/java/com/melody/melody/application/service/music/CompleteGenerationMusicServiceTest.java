package com.melody.melody.application.service.music;

import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.melody.melody.domain.model.TestMusicDomainGenerator.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class CompleteGenerationMusicServiceTest {

    @InjectMocks
    private CompleteGenerationMusicService service;

    @Mock private MusicRepository repository;

    @Test
    void execute_ShouldCompletionMusic() {

        Music music = generatedMusic();
        Music.MusicId id = randomMusicId();
        music = insertMusicId(music, id);

        Music expectedMusic = cloneMusic(music);
        expectedMusic.completeGeneration();

        CompleteGenerationMusicService.Command command = new CompleteGenerationMusicService.Command(id);

        when(repository.getById(id))
                .thenReturn(Optional.of(music));

        CompleteGenerationMusicService.Result result = service.execute(command);

        Music actualMusic;
        assertNotNull((actualMusic = result.getMusic()));
        assertEquals(expectedMusic, actualMusic);

        verify(repository, times(1)).getById(id);
    }

    @Test
    void execute_ThrowException_WhenNonexistentMusic() {
        Music.MusicId id = randomMusicId();
        CompleteGenerationMusicService.Command command = new CompleteGenerationMusicService.Command(id);

        when(repository.getById(id))
                .thenReturn(Optional.empty());

        assertException(
                () -> service.execute(command),
                NotFoundException.class,
                DomainError.of(MusicErrorType.Not_Found_Music)
                );

        verify(repository, times(1)).getById(id);
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}