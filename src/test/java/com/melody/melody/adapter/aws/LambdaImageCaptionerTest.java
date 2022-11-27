package com.melody.melody.adapter.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.aws.response.EmotionMapping;
import com.melody.melody.adapter.aws.response.LambdaEmotionClassifitionResponse;
import com.melody.melody.adapter.aws.response.LambdaImageCaptioningResponse;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LambdaImageCaptionerTest {

    private LambdaImageCaptioner captioner;

    private ObjectMapper objectMapper;
    private MockWebServer mockWebServer;
    private Dispatcher dispatcher;
    private MockResponse response;

    private final String responseCaption = "이미지 설명~!";

    @BeforeEach
    void setUp() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        mockWebServer = new MockWebServer();

        response = new MockResponse()
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(
                        objectMapper.writeValueAsString(
                                LambdaImageCaptioningResponse.builder().caption(responseCaption).build()
                        )
                );
        dispatcher = new Dispatcher() {
            @NotNull
            public MockResponse dispatch(@NotNull RecordedRequest request) {
                return response;
            }
        };

        mockWebServer.setDispatcher(dispatcher);

        captioner = new LambdaImageCaptioner();
        String mockServerUrl = mockWebServer.url("/").toString();
        ReflectionTestUtils.setField(captioner, "uri", mockServerUrl);
    }

    @Test
    void execute_ShouldReturnEmotion() {
        Music.ImageUrl imageUrl = TestMusicDomainGenerator.randomImageUrl();

        Music.Explanation explanation = captioner.execute(imageUrl);

        assertNotNull(explanation);
        assertEquals(responseCaption, explanation.getValue());

    }
}