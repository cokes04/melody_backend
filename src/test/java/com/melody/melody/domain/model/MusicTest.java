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
    public void generate_ReturnCreatedMusic(){
        String userId = String.valueOf(TestUserDomainGenerator.randomUserId());
        Emotion emotion = randomEmotion();
        Music.Explanation explanation = randomExplanation();
        Music.ImageUrl imageUrl = randomImageUrl();

        Music actual = Music.generate(userId, emotion, explanation, imageUrl);

        assertEquals(userId, actual.getUserId());
        assertTrue(actual.getId().isEmpty());
        assertEquals(Music.Status.PROGRESS, actual.getStatus());
        assertTrue(actual.getMusicUrl().isEmpty());
        assertEquals(emotion, actual.getEmotion());
        assertEquals(explanation, actual.getExplanation());
        assertEquals(imageUrl, actual.getImageUrl());
    }

    @Test
    void completeGeneration_ShouldChangeCompletion() {
        Music music = generatedMusic();
        Music.MusicUrl musicUrl = randomMusicUrl();

        assertEquals(Music.Status.PROGRESS, music.getStatus());

        music.completeGeneration(musicUrl);

        assertEquals(Music.Status.COMPLETION, music.getStatus());
        assertTrue(music.getMusicUrl().isPresent());
        assertEquals(musicUrl, music.getMusicUrl().get());

    }

    @Test
    void completeGeneration_ShouldThrowException_WhenCompletion() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();
        Music music = randomCompletionMusic(userId);
        Music.MusicUrl musicUrl = randomMusicUrl();

        assertEquals(Music.Status.COMPLETION, music.getStatus());

        assertException(() -> music.completeGeneration(musicUrl),
                InvalidStatusException.class,
                DomainError.of(MusicErrorType.Should_Be_Progress_State_For_Complete_Generation));
    }

    @Test
    void completeGeneration_ShouldThrowException_WhenDeleted() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();
        Music music = randomDeletedMusic(userId);
        Music.MusicUrl musicUrl = randomMusicUrl();

        assertEquals(Music.Status.DELETED, music.getStatus());

        assertException(() -> music.completeGeneration(musicUrl),
                InvalidStatusException.class,
                DomainError.of(MusicErrorType.Should_Be_Progress_State_For_Complete_Generation));
    }

    @Test
    void delete_ShouldChangeDeleted() {
        Music music = TestMusicDomainGenerator.randomProgressMusic();

        assertNotEquals(Music.Status.DELETED, music.getStatus());
        music.delete();
        assertEquals(Music.Status.DELETED, music.getStatus());

    }

    @Test
    void delete_ShouldhrowException_WhenDeletedMusic() {
        Music music = TestMusicDomainGenerator.randomDeletedMusic();

        assertEquals(Music.Status.DELETED, music.getStatus());

        assertException(
                music::delete,
                InvalidStatusException.class,
                DomainError.of(MusicErrorType.Music_Already_Deleted)
        );

    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
            }
}