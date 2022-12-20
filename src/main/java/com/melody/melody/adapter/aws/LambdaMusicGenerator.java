package com.melody.melody.adapter.aws;

import com.melody.melody.adapter.aws.request.LambdaGenerateMusicRequest;
import com.melody.melody.adapter.aws.response.EmotionMapping;
import com.melody.melody.application.port.out.MusicGenerator;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class LambdaMusicGenerator implements MusicGenerator {
    private final static Consumer<HttpHeaders> asyncDefaultHeaderConsumer;

    private WebClient webClient;

    @Value("${cloud.aws.lambda.generateMusic.uri}")
    private String uri;

    static {
        asyncDefaultHeaderConsumer = (httpHeaders) -> {
            httpHeaders.add("InvocationType", "Event");
            httpHeaders.add("Content-Type", "application/json");
        };
    }

    {
        this.webClient = WebClient.builder()
                .defaultHeaders(asyncDefaultHeaderConsumer)
                .build();
    }

    @Override
    public void executeAsync(Music.MusicId musicId, Emotion emotion, int musicLength, int noise) {
        LambdaGenerateMusicRequest request = LambdaGenerateMusicRequest.builder()
                .musicId(musicId.getValue())
                .emotion(EmotionMapping.of(emotion).getGeneration())
                .musicLength(musicLength)
                .noise(noise)
                .build();


        webClient.post()
                .uri(uri)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        (status) -> !status.is2xxSuccessful(),
                        clientResponse -> clientResponse
                                .createException()
                                .flatMap( e ->
                                        Mono.error(
                                                new FailedGenerateMusicException(DomainError.of(AwsErrorType.Failed_Generate_Music)
                                                )
                                )
                        )
                )
                .bodyToMono(Void.class)
                .block();
    }
}
