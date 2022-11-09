package com.melody.melody.adapter.web.music.request;

import com.melody.melody.application.service.music.GenerateMusicService;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static com.melody.melody.adapter.web.music.TestMusicWebGenerator.randomMultipartFile;
import static org.junit.jupiter.api.Assertions.*;

class GenerateMusicRequestTest {

    @Test
    void toCommand_returnCreatedCommand() {
        int musicLength = 50;
        int noise = 33;
        GenerateMusicRequest generateMusicRequest = GenerateMusicRequest.builder().musicLength(musicLength).noise(noise).build();
        MultipartFile image = randomMultipartFile();

        GenerateMusicService.Command actual = generateMusicRequest.toCommand(image);

        assertEquals(image.getContentType(), actual.getImage().getMediaType());
        assertEquals(image.getResource(), actual.getImage().getResource());
        assertEquals(image.getSize(), actual.getImage().getSize());
        assertEquals(musicLength, actual.getMusicLength());
        assertEquals(noise, actual.getNoise());
    }
}