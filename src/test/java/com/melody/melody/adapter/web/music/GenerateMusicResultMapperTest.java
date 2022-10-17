package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.response.MusicResponse;
import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.melody.melody.domain.model.TestDomainGenerator.randomMusic;
import static org.junit.jupiter.api.Assertions.*;

class GenerateMusicResultMapperTest {

    private static GenerateMusicResultMapper mapper;


    @BeforeAll
    static void beforeAll() {
        mapper = new GenerateMusicResultMapper();
    }

    @Test
    void returnCreatedMusicResonse() {
        Music music = randomMusic();
        GenerateMusicService.Result result = new GenerateMusicService.Result(music);

        MusicResponse musicResponse = mapper.to(result);

        assertEquals(music.getEmotion().name().toLowerCase(), musicResponse.getEmotion());
        assertEquals(music.getExplanation().getValue(), musicResponse.getExplanation());
        assertEquals(music.getImageUrl().getValue(), musicResponse.getImageUrl());
        assertEquals(music.getId().get().getValue(), musicResponse.getMusicId());
        assertEquals(music.getStatus().name().toLowerCase(), musicResponse.getStatus());
    }
}