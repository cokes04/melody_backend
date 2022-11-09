package com.melody.melody.adapter.aws;

import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import static com.melody.melody.application.service.music.TestMusicServiceGenerator.*;
import static com.melody.melody.domain.model.TestMusicDomainGenerator.*;

@ExtendWith(MockitoExtension.class)
class LambdaMusicGeneratorTest {

    private LambdaMusicGenerator generator;

    private MockWebServer mockWebServer;
    private Dispatcher dispatcher;
    private MockResponse response;

    @BeforeEach
    void setUp() {
        mockWebServer = new MockWebServer();

        response = new MockResponse()
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value());

         dispatcher = new Dispatcher() {
            @NotNull
            public MockResponse dispatch(@NotNull RecordedRequest request) {
                return response;
            }
        };

        mockWebServer.setDispatcher(dispatcher);

        generator = new LambdaMusicGenerator();
        String mockServerUrl = mockWebServer.url("/").toString();
        ReflectionTestUtils.setField(generator, "uri", mockServerUrl);
    }

    @Test
    void executeAsync(){

        Music.MusicId musicId = randomMusicId();
        Emotion emotion = randomEmotion();
        int musicLength = randomMusicLength();
        int noise = randomNoise();

        generator.executeAsync(musicId, emotion, musicLength, noise);
    }
}
