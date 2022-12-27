package com.melody.melody.adapter.web.music.request;

import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static com.melody.melody.adapter.web.music.TestMusicWebGenerator.randomMultipartFile;
import static org.junit.jupiter.api.Assertions.*;

class GenerateMusicRequestTest {

    @Test
    void toCommand_returnCreatedCommand() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        int musicLength = 50;
        int noise = 33;
        GenerateMusicRequest generateMusicRequest = GenerateMusicRequest.builder().musicLength(musicLength).noise(noise).build();
        MultipartFile image = randomMultipartFile();

        GenerateMusicService.Command actual = generateMusicRequest.toCommand(image, userId.getValue());

        assertEquals(userId.getValue(), actual.getUserId());
        assertEquals(image.getContentType(), actual.getImage().getMediaType());
        assertEquals(image.getResource(), actual.getImage().getResource());
        assertEquals(image.getSize(), actual.getImage().getSize());
        assertEquals(musicLength, actual.getMusicLength());
        assertEquals(noise, actual.getNoise());
    }
}