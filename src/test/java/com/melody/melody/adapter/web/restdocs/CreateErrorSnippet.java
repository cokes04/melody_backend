package com.melody.melody.adapter.web.restdocs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.aws.AwsErrorType;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.exception.type.DomainErrorType;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Emotion;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
public class CreateErrorSnippet {

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
                body.put("errors", getErrorTypes());

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
    void createErrorSnippet() throws Exception {
        webClient.get()
                .uri("/")
                .exchange().expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(
                        document(
                                "error-docs",
                                new CustomFieldSnippet("error-code",
                                        beneathPath("errors").withSubsectionId("errors"),
                                        Arrays.asList(enumConvertFieldDescriptor(getErrorTypes())),
                                        attributes(key("title").value("에러 목록")),
                                        true
                                )
                        )
                );

    }

    private Map<String, String> getErrorTypes() {
        Map<String,String> map = new HashMap<>();
        put(map, MusicErrorType.values());
        put(map, PostErrorType.values());
        put(map, UserErrorType.values());
        put(map, AwsErrorType.values());
        map.put("400001", String.format(AwsErrorType.Not_Supported_Media_Type.getMessageFormat(), "image/bmp"));

        return map;
    }

    private void put(Map<String, String> map, DomainErrorType[] errorTypes){
        for (DomainErrorType errorType : errorTypes){
            map.put(errorType.getCode(), errorType.getMessageFormat());
        }
    }

    private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
                .toArray(FieldDescriptor[]::new);
    }
}
