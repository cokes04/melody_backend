package com.melody.melody.adapter.web.post;

import com.melody.melody.adapter.web.post.request.CreatePostRequest;
import com.melody.melody.adapter.web.post.request.UpdatePostRequest;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import net.datafaker.Faker;

public class TestPostWebGenerator {
    private final static Faker faker = new Faker();

    public static CreatePostRequest randomCreatePostRequest(){
        return CreatePostRequest.builder()
                .musicId(TestMusicDomainGenerator.randomMusicId().getValue())
                .title(TestPostDomainGenerator.randomTitle().getValue())
                .content(TestPostDomainGenerator.randomContent().getValue())
                .open(true)
                .build();
    }

    public static UpdatePostRequest randomUpdatePostRequest(){
        return UpdatePostRequest.builder()
                .title(TestPostDomainGenerator.randomTitle().getValue())
                .content(TestPostDomainGenerator.randomContent().getValue())
                .open(true)
                .build();
    }
}
