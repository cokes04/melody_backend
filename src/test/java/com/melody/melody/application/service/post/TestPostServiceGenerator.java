package com.melody.melody.application.service.post;


import com.melody.melody.domain.model.*;
import net.datafaker.Faker;

public class TestPostServiceGenerator {
    private static final Faker faker = new Faker();

    public static UpdatePostService.Command randomUpdatePostCommand(){
        return randomUpdatePostService(TestPostDomainGenerator.randomPostId().getValue());
    }

    public static CreatePostService.Command randomCreatePostCommand(){
        return randomCreatePostCommand(
                TestUserDomainGenerator.randomUserId().getValue(),
                TestMusicDomainGenerator.randomMusicId().getValue()
        );
    }

    public static UpdatePostService.Command randomUpdatePostService(long postId){
        return new UpdatePostService.Command(
                postId,
                TestPostDomainGenerator.randomTitle().getValue(),
                TestPostDomainGenerator.randomContent().getValue(),
                true
        );
    }

    public static CreatePostService.Command randomCreatePostCommand(long userId, long musicId){
        return new CreatePostService.Command(
                userId,
                musicId,
                TestPostDomainGenerator.randomTitle().getValue(),
                TestPostDomainGenerator.randomContent().getValue(),
                true
        );
    }
}
