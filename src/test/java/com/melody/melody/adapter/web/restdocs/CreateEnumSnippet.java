package com.melody.melody.adapter.web.restdocs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.application.dto.MusicPublish;
import com.melody.melody.application.dto.MusicSort;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.model.Music;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@WebFluxTest(controllers = TestController.class)
public class CreateEnumSnippet {

    private WebTestClient webClient;

    private ObjectMapper objectMapper;
    private MockWebServer mockWebServer;
    private Dispatcher dispatcher;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        mockWebServer = new MockWebServer();

        dispatcher = new Dispatcher() {
            @SneakyThrows
            @NotNull
            public MockResponse dispatch(@NotNull RecordedRequest request) {
                Map<String, Map<String, String>> body = new HashMap<>();
                body.put("emotion", getEmotion());
                body.put("musicStatus", getMusicStatus());
                body.put("postSort", getPostSort());
                body.put("musicSort", getMusicSort());
                body.put("musicPublish", getMusicPublish());

                return new MockResponse()
                        .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.OK.value())
                        .setBody(objectMapper.writeValueAsString(body));
            }
        };

        mockWebServer.setDispatcher(dispatcher);

        webClient = WebTestClient
                .bindToServer()
                .baseUrl(mockWebServer.url("/").toString())
                .filter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void createEnumSnippet() throws Exception {
        webClient.get()
                .uri("/")
                .exchange().expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(
                        document(
                                "enum-docs",
                                new CustomFieldSnippet("code",
                                        beneathPath("emotion").withSubsectionId("emotion"),
                                        Arrays.asList(enumConvertFieldDescriptor(getEmotion())),
                                        attributes(key("title").value("?????? ??????")),
                                        true
                                ),
                                new CustomFieldSnippet("code",
                                        beneathPath("musicStatus").withSubsectionId("musicStatus"),
                                        Arrays.asList(enumConvertFieldDescriptor(getMusicStatus())),
                                        attributes(key("title").value("?????? ??????")),
                                        true
                                ),
                                new CustomFieldSnippet("code",
                                        beneathPath("postSort").withSubsectionId("postSort"),
                                        Arrays.asList(enumConvertFieldDescriptor(getPostSort())),
                                        attributes(key("title").value("????????? ?????? ??????")),
                                        true
                                ),
                                new CustomFieldSnippet("code",
                                        beneathPath("musicSort").withSubsectionId("musicSort"),
                                        Arrays.asList(enumConvertFieldDescriptor(getMusicSort())),
                                        attributes(key("title").value("?????? ?????? ??????")),
                                        true
                                ),
                                new CustomFieldSnippet("code",
                                        beneathPath("musicPublish").withSubsectionId("musicPublish"),
                                        Arrays.asList(enumConvertFieldDescriptor(getMusicPublish())),
                                        attributes(key("title").value("?????? ?????? ??????")),
                                        true
                                )
                        )
                );

    }

    private Map<String, String> getMusicStatus() {
        Map<String,String> map = new HashMap<>();
        map.put(Music.Status.PROGRESS.name().toLowerCase(), "?????? ?????????");
        map.put(Music.Status.COMPLETION.name().toLowerCase(), "?????? ?????????");
        map.put(Music.Status.DELETED.name().toLowerCase(), "?????? ?????????");

        return map;
    }

    private Map<String, String> getEmotion() {
        Map<String,String> map = new HashMap<>();
        map.put(Music.Emotion.RELAXED.name().toLowerCase(), "?????????");
        map.put(Music.Emotion.DELIGHTED.name().toLowerCase(), "??????");
        map.put(Music.Emotion.GLOOMY.name().toLowerCase(), "??????");
        map.put(Music.Emotion.TENSE.name().toLowerCase(), "?????????");

        return map;
    }

    private Map<String, String> getPostSort() {
        Map<String,String> map = new HashMap<>();
        map.put(PostSort.newest.name().toLowerCase(), "????????? ?????????");
        map.put(PostSort.oldest.name().toLowerCase(), "????????? ????????????");

        return map;
    }

    private Map<String, String> getMusicSort() {
        Map<String,String> map = new HashMap<>();
        map.put(MusicSort.newest.name().toLowerCase(), "?????? ?????????");
        map.put(MusicSort.oldest.name().toLowerCase(), "?????? ????????????");

        return map;
    }

    private Map<String, String> getMusicPublish() {
        Map<String,String> map = new HashMap<>();
        map.put(MusicPublish.Everything.name().toLowerCase(), "????????? ?????? ?????? ??????");
        map.put(MusicPublish.Published.name().toLowerCase(), "????????? ???????????? ????????? ??????");
        map.put(MusicPublish.Unpublished.name().toLowerCase(), "????????? ???????????? ???????????? ?????? ??????");

        return map;
    }


    private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
                .map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
                .toArray(FieldDescriptor[]::new);
    }
}
