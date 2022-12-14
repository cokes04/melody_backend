package com.melody.melody.application.handler;

import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.event.MusicComposed;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.melody.melody.domain.model.TestMusicDomainGenerator.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MusicComposedEventHandlerTest {

    private MusicComposedEventHandler service;

    private MusicRepository repository;


    @BeforeEach
    void setUp() {
        repository = Mockito.mock(MusicRepository.class);
        service = new MusicComposedEventHandler(repository);
    }

    @Test
    void execute_ShouldCompletionMusic() {
        Music music = generatedMusic();
        Music.MusicId id = randomMusicId();
        music = insertMusicId(music, id);
        Music.MusicUrl musicUrl = randomMusicUrl();

        Music expectedMusic = cloneMusic(music);
        expectedMusic.completeGeneration(musicUrl);

        MusicComposed musicComposed = new MusicComposed(id.getValue(), musicUrl.getValue());

        when(repository.findById(id))
                .thenReturn(Optional.of(music));
        when(repository.save(any(Music.class)))
                .thenAnswer(a -> a.getArgument(0, Music.class));

        service.handle(musicComposed);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(Music.class));
    }

    @Test
    void execute_ThrowException_WhenNonexistentMusic() {
        Music.MusicId id = randomMusicId();
        Music.MusicUrl musicUrl = randomMusicUrl();
        MusicComposed musicComposed = new MusicComposed(id.getValue(), musicUrl.getValue());


        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertException(
                () -> service.handle(musicComposed),
                NotFoundException.class,
                DomainError.of(MusicErrorType.Not_Found_Music)
                );

        verify(repository, times(1)).findById(id);
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}