package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.response.MusicResponse;
import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.melody.melody.domain.model.TestDomainGenerator.randomMusic;
import static org.junit.jupiter.api.Assertions.*;

class MusicResponseMapperTest {

    private static MusicResponseMapper mapper;


    @BeforeAll
    static void beforeAll() {
        mapper = new MusicResponseMapper();
    }

    @Test
    void returnCreatedMusicResonse() {
        Music music = randomMusic();

        MusicResponse musicResponse = mapper.to(music);

        assertEquals(music.getEmotion().name().toLowerCase(), musicResponse.getEmotion());
        assertEquals(music.getExplanation().getValue(), musicResponse.getExplanation());
        assertEquals(music.getImageUrl().getValue(), musicResponse.getImageUrl());
        assertEquals(music.getId().get().getValue(), musicResponse.getMusicId());
        assertEquals(music.getStatus().name().toLowerCase(), musicResponse.getStatus());
    }
}