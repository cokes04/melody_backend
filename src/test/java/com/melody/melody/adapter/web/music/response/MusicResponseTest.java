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

        assertEquals(music.getEmotion(), musicResponse.getEmotion());
        assertEquals(music.getExplanation().getValue(), musicResponse.getExplanation());
        assertEquals(music.getImageUrl().getValue(), musicResponse.getImageUrl());
        assertEquals(music.getId().getValue(), musicResponse.getMusicId());
        assertEquals(music.getUserId().getValue(), musicResponse.getUserId());
        assertEquals(music.getStatus(), musicResponse.getStatus());
        assertEquals(music.getMusicUrl().getValue(), musicResponse.getMusicUrl());
    }
}