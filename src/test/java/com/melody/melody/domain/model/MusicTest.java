package com.melody.melody.domain.model;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.MusicErrorType;
import org.junit.jupiter.api.Test;

import static com.melody.melody.domain.model.TestDomainGenerator.*;
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
        assertEquals(emotion, actual.getEmotion());
        assertEquals(explanation, actual.getExplanation());
        assertEquals(imageUrl, actual.getImageUrl());
    }

    @Test
    void completeGeneration() {
        Music music = generatedMusic();
        assertEquals(Music.Status.PROGRESS, music.getStatus());

        music.completeGeneration();

        assertEquals(Music.Status.COMPLETION, music.getStatus());

    }

    @Test
    void completeGeneration_ThrowException_WhenStatusIsNotPROGRESS() {
        Music music = generatedMusic();
        music.completeGeneration();

        assertNotEquals(Music.Status.PROGRESS, music.getStatus());
        assertEquals(Music.Status.COMPLETION, music.getStatus());

        assertException(music::completeGeneration,
                InvalidStatusException.class,
                DomainError.of(MusicErrorType.Should_Be_Progress_State_For_Complete_Generation));
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
            }
}