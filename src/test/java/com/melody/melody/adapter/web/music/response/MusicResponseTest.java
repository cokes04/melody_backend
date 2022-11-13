package com.melody.melody.adapter.web.music.response;

import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.Test;

import static com.melody.melody.domain.model.TestMusicDomainGenerator.randomMusic;
import static org.junit.jupiter.api.Assertions.*;

class MusicResponseTest {

    @Test
    void to_returnCreatedMusicResonse() {
        Music music = randomMusic();

        MusicResponse musicResponse = MusicResponse.to(music);

        assertEquals(music.getEmotion().name().toLowerCase(), musicResponse.getEmotion());
        assertEquals(music.getExplanation().getValue(), musicResponse.getExplanation());
        assertEquals(music.getImageUrl().getValue(), musicResponse.getImageUrl());
        assertTrue(music.getId().isPresent());
        assertEquals(music.getId().get().getValue(), musicResponse.getMusicId());
        assertEquals(music.getStatus().name().toLowerCase(), musicResponse.getStatus());
        assertTrue(music.getMusicUrl().isPresent());
        assertEquals(music.getMusicUrl().get().getValue(), musicResponse.getMusicUrl());
    }
}