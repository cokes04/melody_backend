package com.melody.melody.adapter.aws;

import com.melody.melody.adapter.aws.request.LambdaEmotionclassificationRequest;
import com.melody.melody.adapter.aws.response.EmotionMapping;
import com.melody.melody.adapter.aws.response.LambdaEmotionClassifitionResponse;
import com.melody.melody.application.port.out.EmotionClassifier;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class LambdaEmotionClassifier implements EmotionClassifier {
    private final static Consumer<HttpHeaders> defaultHeaderConsumer;

    private WebClient webClient;

    @Value("${cloud.aws.lambda.emotionClassification.uri}")
    private String uri;

    static {
        defaultHeaderConsumer = (httpHeaders) -> {
            httpHeaders.add("Content-Type", "application/json");
        };
    }

    {
        this.webClient = WebClient.builder()
                .defaultHeaders(defaultHeaderConsumer)
                .build();
    }

    @Override
    public Music.Emotion execute(Music.Explanation caption) {
        LambdaEmotionclassificationRequest request = LambdaEmotionclassificationRequest.builder()
                .caption(caption.getValue())
                .build();

        return webClient.post()
                .uri(uri)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LambdaEmotionClassifitionResponse.class)
                .map(
                        res -> EmotionMapping.of(res.getEmotion()).getEmotion()
                        )
                .block();
    }
}
