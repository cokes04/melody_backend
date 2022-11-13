package com.melody.melody.domain.model;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.MusicErrorType;
import org.junit.jupiter.api.Test;

import static com.melody.melody.domain.model.TestMusicDomainGenerator.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class MusicTest {

    @Test
    public void generate(){
        Emotion emotion = randomEmotion();
        Music.Explanation explanation = randomExplanation();
        Music.ImageUrl imageUrl = randomImageUrl();

        Music actual = Music.generate(emotion, explanation, imageUrl);

        assertTrue(actual.getId().isEmpty());
        assertEquals(Music.Status.PROGRESS, actual.getStatus());
        assertTrue(actual.getMusicUrl().isEmpty());
        assertEquals(emotion, actual.getEmotion());
        assertEquals(explanation, actual.getExplanation());
        assertEquals(imageUrl, actual.getImageUrl());
    }

    @Test
    void completeGeneration() {
        Music music = generatedMusic();
        Music.MusicUrl musicUrl = randomMusicUrl();

        assertEquals(Music.Status.PROGRESS, music.getStatus());

        music.completeGeneration(musicUrl);

        assertEquals(Music.Status.COMPLETION, music.getStatus());
        assertTrue(music.getMusicUrl().isPresent());
        assertEquals(musicUrl, music.getMusicUrl().get());

    }

    @Test
    void completeGeneration_ThrowException_WhenStatusIsNotPROGRESS() {
        Music music = generatedMusic();
        Music.MusicUrl musicUrl = randomMusicUrl();
        Music.MusicUrl newMusicUrl = randomMusicUrl();

        music.completeGeneration(musicUrl);

        assertNotEquals(Music.Status.PROGRESS, music.getStatus());
        assertEquals(Music.Status.COMPLETION, music.getStatus());

        assertException(() -> music.completeGeneration(newMusicUrl),
                InvalidStatusException.class,
                DomainError.of(MusicErrorType.Should_Be_Progress_State_For_Complete_Generation));
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
            }
}