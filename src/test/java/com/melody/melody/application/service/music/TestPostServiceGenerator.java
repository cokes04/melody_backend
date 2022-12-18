package com.melody.melody.application.service.music;

import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.application.service.post.CreatePostService;
import com.melody.melody.application.service.post.UpdatePostService;
import com.melody.melody.domain.model.*;
import net.datafaker.Faker;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

import static com.melody.melody.domain.model.TestMusicDomainGenerator.randomMusic;

public class TestPostServiceGenerator {
    private static final Faker faker = new Faker();

    public static UpdatePostService.Command randomUpdatePostCommand(){
        return randomUpdatePostService(TestPostDomainGenerator.randomPostId());
    }

    public static CreatePostService.Command randomCreatePostCommand(){
        return randomCreatePostCommand(
                TestUserDomainGenerator.randomUserId(),
                TestMusicDomainGenerator.randomMusicId()
        );
    }

    public static UpdatePostService.Command randomUpdatePostService(Post.PostId postId){
        return new UpdatePostService.Command(
                postId,
                TestPostDomainGenerator.randomTitle().getValue(),
                TestPostDomainGenerator.randomContent().getValue(),
                true
        );
    }

    public static CreatePostService.Command randomCreatePostCommand(User.UserId userId, Music.MusicId musicId){
        return new CreatePostService.Command(
                userId,
                musicId,
                TestPostDomainGenerator.randomTitle().getValue(),
                TestPostDomainGenerator.randomContent().getValue(),
                true
        );
    }
}
