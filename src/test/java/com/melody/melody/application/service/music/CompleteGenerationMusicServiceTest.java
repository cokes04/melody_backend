package com.melody.melody.application.service.music;

import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.melody.melody.domain.model.TestMusicDomainGenerator.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CompleteGenerationMusicServiceTest {

    private CompleteGenerationMusicService service;

    private MusicRepository repository;


    @BeforeEach
    void setUp() {
        repository = Mockito.mock(MusicRepository.class);
        service = new CompleteGenerationMusicService(repository);
    }

    @Test
    void execute_ShouldCompletionMusic() {
        Music music = generatedMusic();
        Music.MusicId id = randomMusicId();
        music = insertMusicId(music, id);
        Music.MusicUrl musicUrl = randomMusicUrl();

        Music expectedMusic = cloneMusic(music);
        expectedMusic.completeGeneration(musicUrl);

        CompleteGenerationMusicService.Command command = new CompleteGenerationMusicService.Command(id, musicUrl);

        when(repository.getById(id))
                .thenReturn(Optional.of(music));
        when(repository.save(any(Music.class)))
                .thenAnswer(a -> a.getArgument(0, Music.class));

        CompleteGenerationMusicService.Result result = service.execute(command);

        Music actualMusic;
        assertNotNull((actualMusic = result.getMusic()));
        assertEquals(expectedMusic, actualMusic);

        verify(repository, times(1)).getById(id);
        verify(repository, times(1)).save(any(Music.class));
    }

    @Test
    void execute_ThrowException_WhenNonexistentMusic() {
        Music.MusicId id = randomMusicId();
        Music.MusicUrl musicUrl = randomMusicUrl();

        CompleteGenerationMusicService.Command command = new CompleteGenerationMusicService.Command(id, musicUrl);

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