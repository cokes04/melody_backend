package com.melody.melody.application.service.post;

import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import net.datafaker.Faker;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

import static com.melody.melody.domain.model.TestMusicDomainGenerator.randomMusic;

public class TestPostServiceGenerator {
    private static final Faker faker = new Faker();

    public static CreatePostService.Command randomCreatePostCommand(){
        return new CreatePostService.Command(
                TestUserDomainGenerator.randomUserId(),
                TestMusicDomainGenerator.randomMusicId(),

                TestPostDomainGenerator.randomTitle().getValue(),
                TestPostDomainGenerator.randomContent().getValue(),
                true
        );
    }
}
