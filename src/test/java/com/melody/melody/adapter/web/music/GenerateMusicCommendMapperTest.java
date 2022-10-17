package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.request.MusicRequest;
import com.melody.melody.application.service.music.GenerateMusicService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static com.melody.melody.adapter.web.TestWebGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

class GenerateMusicCommendMapperTest {


    private static GenerateMusicCommendMapper mapper;

    @BeforeAll
    static void beforeAll() {
        mapper = new GenerateMusicCommendMapper();
    }

    @Test
    void returnCreatedCommand() {
        int musicLength = 50;
        int noise = 33;
        MusicRequest musicRequest = MusicRequest.builder().musicLength(musicLength).noise(noise).build();
        MultipartFile image = randomMultipartFile();

        GenerateMusicService.Command actual = mapper.of(musicRequest, image);

        assertEquals(image.getContentType(), actual.getImage().getMediaType());
        assertEquals(image.getResource(), actual.getImage().getResource());
        assertEquals(image.getSize(), actual.getImage().getSize());
        assertEquals(musicLength, actual.getMusicLength());
        assertEquals(noise, actual.getNoise());
    }
}