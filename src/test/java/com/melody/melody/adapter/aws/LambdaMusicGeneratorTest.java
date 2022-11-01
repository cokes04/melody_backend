package com.melody.melody.adapter.aws;

import com.melody.melody.config.JasyptConfig;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static com.melody.melody.application.service.TestServiceGenerator.*;
import static com.melody.melody.domain.model.TestDomainGenerator.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = "dev")
@EnableEncryptableProperties
@ContextConfiguration(classes = {JasyptConfig.class})
@SpringBootTest(classes = {LambdaMusicGenerator.class})
class LambdaMusicGeneratorTest {

    @Autowired
    private LambdaMusicGenerator generator;

    @Test
    void executeAsync_clientSuccess() {
        Music.MusicId musicId = randomMusicId();
        Emotion emotion = randomEmotion();
        int musicLength = randomMusicLength();
        int noise = randomNoise();

        generator.executeAsync(musicId, emotion, musicLength, noise);
    }
}
