package com.melody.melody.adapter.web;

import com.melody.melody.adapter.web.response.MusicResponse;
import com.melody.melody.domain.model.TestDomainGenerator;
import net.datafaker.Faker;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

public class TestWebGenerator {
    private static final Faker faker = new Faker();

    public static MultipartFile randomMultipartFile(){
        return new MockMultipartFile(
                        "file",
                        "testtesttest.png",
                        MediaType.IMAGE_PNG_VALUE,
                        "<<png data>>".getBytes(StandardCharsets.UTF_8));
    }

    public static MusicResponse randomMusicResponse(){
        return MusicResponse.builder()
                .musicId(TestDomainGenerator.randomMusicId().getValue())
                .imageUrl(TestDomainGenerator.randomImageUrl().getValue())
                .explanation(TestDomainGenerator.randomExplanation().getValue())
                .emotion(TestDomainGenerator.randomEmotion().name().toLowerCase())
                .status(TestDomainGenerator.randomStatus().name().toLowerCase())
                .build();
    }
}
