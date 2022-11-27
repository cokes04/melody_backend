package com.melody.melody.adapter.aws;

import com.melody.melody.adapter.aws.request.LambdaImageCaptioningRequest;
import com.melody.melody.adapter.aws.response.LambdaImageCaptioningResponse;
import com.melody.melody.application.port.out.ImageCaptioner;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class LambdaImageCaptioner implements ImageCaptioner {
    private final static Consumer<HttpHeaders> defaultHeaderConsumer;

    private WebClient webClient;

    @Value("${aws.lambda.imageCaptioning.uri}")
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
    public Music.Explanation execute(Music.ImageUrl imageUrl) {
        LambdaImageCaptioningRequest request = LambdaImageCaptioningRequest.builder().imageUri(imageUrl.getValue()).build();

        return webClient.post()
                .uri(uri)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LambdaImageCaptioningResponse.class)
                .map(
                        res -> new Music.Explanation(res.getCaption())
                )
                .block();
    }
}