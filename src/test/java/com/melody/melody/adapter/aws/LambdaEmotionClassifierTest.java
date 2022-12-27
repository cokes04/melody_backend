package com.melody.melody.adapter.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.aws.response.EmotionMapping;
import com.melody.melody.adapter.aws.response.LambdaEmotionClassifitionResponse;
import com.melody.melody.domain.model.Music;
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
class LambdaEmotionClassifierTest {

    private LambdaEmotionClassifier classifier;

    private ObjectMapper objectMapper;
    private MockWebServer mockWebServer;
    private Dispatcher dispatcher;
    private MockResponse response;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        mockWebServer = new MockWebServer();

        response = new MockResponse()
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
        .setBody(
                objectMapper.writeValueAsString(
                        LambdaEmotionClassifitionResponse.builder().emotion(EmotionMapping.of(Music.Emotion.RELAXED).getClassification()).build()
                )
        );

        dispatcher = new Dispatcher() {
            @NotNull
            public MockResponse dispatch(@NotNull RecordedRequest request) {
                return response;
            }
        };

        mockWebServer.setDispatcher(dispatcher);

        classifier = new LambdaEmotionClassifier();
        String mockServerUrl = mockWebServer.url("/").toString();
        ReflectionTestUtils.setField(classifier, "uri", mockServerUrl);
    }
    @Test
    void execute_ShouldReturnEmotion() {
        Music.Explanation caption = new Music.Explanation("안녕하다 행복하다");

        Music.Emotion emotion = classifier.execute(caption);

        assertNotNull(emotion);

    }
}