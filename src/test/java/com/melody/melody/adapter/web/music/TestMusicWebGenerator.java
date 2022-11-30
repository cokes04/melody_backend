package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.music.response.MusicResponse;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import net.datafaker.Faker;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

public class TestMusicWebGenerator {
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
                .musicId(TestMusicDomainGenerator.randomMusicId().getValue())
                .imageUrl(TestMusicDomainGenerator.randomImageUrl().getValue())
                .explanation(TestMusicDomainGenerator.randomExplanation().getValue())
                .emotion(TestMusicDomainGenerator.randomEmotion())
                .status(TestMusicDomainGenerator.randomStatus())
                .build();
    }
}
