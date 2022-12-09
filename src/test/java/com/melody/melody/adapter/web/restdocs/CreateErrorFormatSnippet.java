package com.melody.melody.adapter.web.restdocs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.melody.melody.adapter.web.exception.ErrorResponse;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.type.UserErrorType;
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
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@WebFluxTest(controllers = TestController.class)
public class CreateErrorFormatSnippet {
    private WebTestClient webClient;

    private ObjectMapper objectMapper;
    private MockWebServer mockWebServer;
    private Dispatcher dispatcher;


    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockWebServer = new MockWebServer();

        dispatcher = new Dispatcher() {
            @SneakyThrows
            @NotNull
            public MockResponse dispatch(@NotNull RecordedRequest request) {
                ErrorResponse body = ErrorResponse.to(
                        DomainError.of(UserErrorType.Email_Already_Used)
                );

                return new MockResponse()
                        .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.BAD_REQUEST.value())
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
    void createErrorFormatSnippet() throws Exception {

        webClient.get()
                .uri("/")
                .exchange().expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(
                        document(
                                "error-response-format",
                                responseFields(
                                        fieldWithPath("timestamp").description("에러 발생 시간").type(JsonFieldType.STRING),
                                        fieldWithPath("errors[].code").description("에러 코드").type(JsonFieldType.STRING),
                                        fieldWithPath("errors[].message").description("에러 메시지").type(JsonFieldType.STRING)
                                )
                        )
                );
    }
}
